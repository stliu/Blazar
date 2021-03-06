package com.hubspot.blazar.listener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.kohsuke.github.GHCommitState;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hubspot.blazar.base.GitInfo;
import com.hubspot.blazar.base.RepositoryBuild;
import com.hubspot.blazar.base.github.GitHubApiError;
import com.hubspot.blazar.base.github.GitHubErrorResponse;
import com.hubspot.blazar.base.visitor.RepositoryBuildVisitor;
import com.hubspot.blazar.config.BlazarConfiguration;
import com.hubspot.blazar.config.GitHubConfiguration;
import com.hubspot.blazar.data.service.BranchService;
import com.hubspot.blazar.util.BlazarUrlHelper;
import com.hubspot.blazar.util.GitHubHelper;

@Singleton
public class GitHubStatusVisitor implements RepositoryBuildVisitor {
  private static final Logger LOG = LoggerFactory.getLogger(GitHubStatusVisitor.class);
  private static final String GITHUB_TOO_MANY_STATUSES_MESSAGE = "This SHA and context has reached the maximum number of statuses.";

  private final BranchService branchService;
  private Map<String, GitHubConfiguration> gitHubConfigurationMap;
  private final GitHubHelper gitHubHelper;
  private final BlazarUrlHelper blazarUrlHelper;

  @Inject
  public GitHubStatusVisitor(BranchService branchService,
                             BlazarConfiguration blazarConfiguration,
                             GitHubHelper gitHubHelper,
                             BlazarUrlHelper blazarUrlHelper) {
    this.branchService = branchService;
    this.gitHubConfigurationMap = blazarConfiguration.getGitHubConfiguration();
    this.gitHubHelper = gitHubHelper;
    this.blazarUrlHelper = blazarUrlHelper;
  }

  @Override
  public void visit(RepositoryBuild build) throws Exception {
    // Can't set GitHub status without a sha
    if (!build.getSha().isPresent()) {
      return;
    }

    GitInfo gitInfo = branchService.get(build.getBranchId()).get();
    if (!gitHubConfigurationMap.get(gitInfo.getHost()).getSetCommitStatus()) {
      LOG.info("Git Hub Host {} has setCommitStatus set to false not sending message", gitInfo.getHost());
      return;
    }

    String url = blazarUrlHelper.getBlazarUiLink(build);

    GHCommitState state = toGHCommitState(build.getState());
    String sha = build.getSha().get();
    String description = getStateDescription(build.getState());

    final GHRepository repository;
    try {
      repository = gitHubHelper.repositoryFor(gitInfo);
    } catch (FileNotFoundException e) {
      LOG.warn("Couldn't find repository {}", gitInfo.getFullRepositoryName(), e);
      return;
    }

    LOG.info("Setting status of commit {} to {} for build {}", sha, state, build.getId().get());
    try {
      repository.createCommitStatus(sha, state, url, description, "Blazar");
    } catch (IOException e) {
      Optional<GitHubErrorResponse> gitHubErrorResponse = gitHubHelper.extractErrorResponseFromException(e);
      // GitHub has a limit of 100 statuses you can post to a commit -- since we can't delete statuses we just swallow this error
      if (gitHubErrorResponse.isPresent()) {
        List<GitHubApiError> errors = gitHubErrorResponse.get().getErrors();
        if (errors.stream().filter(er -> er.getMessage().contains(GITHUB_TOO_MANY_STATUSES_MESSAGE)).count() > 0) {
          LOG.warn("Commit {}#{} has the maximum number of statuses GitHub allows, cannot post status.", gitInfo.getFullRepositoryName(), sha);
          return;
        }
      }
      LOG.error("Error setting status of commit {} to {} for build {}", sha, state, build.getId().get(), e);
    }
  }

  private static String getStateDescription(RepositoryBuild.State state) {
    switch (state) {
      case LAUNCHING:
        return "The build is launching";
      case IN_PROGRESS:
        return "The build is in progress";
      case SUCCEEDED:
        return "The build succeeded!";
      case FAILED:
        return "The build failed";
      case CANCELLED:
        return "The build was cancelled";
      case UNSTABLE:
        return "The build succeeded, but other modules are in a failed state";
      default:
        throw new IllegalArgumentException("Unexpected build state: " + state);
    }
  }

  private static GHCommitState toGHCommitState(RepositoryBuild.State state) {
    switch (state) {
      case LAUNCHING:
      case IN_PROGRESS:
        return GHCommitState.PENDING;
      case SUCCEEDED:
        return GHCommitState.SUCCESS;
      case FAILED:
      case UNSTABLE:
        return GHCommitState.FAILURE;
      case CANCELLED:
        return GHCommitState.ERROR;
      default:
        throw new IllegalArgumentException("Unexpected build state: " + state);
    }
  }
}
