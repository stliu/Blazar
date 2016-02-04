package com.hubspot.blazar.listener;

import com.hubspot.blazar.base.ModuleBuild;
import com.hubspot.blazar.base.RepositoryBuild;
import com.hubspot.blazar.base.visitor.AbstractRepositoryBuildVisitor;
import com.hubspot.blazar.data.service.ModuleBuildService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

@Singleton
public class CancelledRepositoryBuildVisitor extends AbstractRepositoryBuildVisitor {
  private final ModuleBuildService moduleBuildService;

  @Inject
  public CancelledRepositoryBuildVisitor(ModuleBuildService moduleBuildService) {
    this.moduleBuildService = moduleBuildService;
  }

  @Override
  protected void visitCancelled(RepositoryBuild repositoryBuild) {
    Set<ModuleBuild> builds = moduleBuildService.getByRepositoryBuild(repositoryBuild.getId().get());
    for (ModuleBuild build : builds) {
      if (!build.getState().isComplete()) {
        moduleBuildService.cancel(build);
      }
    }
  }
}
