package com.hubspot.blazar.externalservice.badbuilds;

import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.inject.Inject;
import com.hubspot.blazar.base.ModuleBuild;
import com.hubspot.blazar.config.BadBuildServiceConfiguration;
import com.hubspot.blazar.config.BlazarConfiguration;
import com.hubspot.blazar.data.service.ModuleBuildService;
import com.hubspot.blazar.data.service.ModuleService;
import com.hubspot.horizon.HttpClient;
import com.hubspot.horizon.HttpRequest;

public class BlazarConfigurationControlledBadBuildClient implements BadBuildClient {

  private final String getBadBuildsByBuildNameEndpoint;
  private final String markBuildAsBadEndpoint;
  private final String unMarkBuildAsBadEndpoint;
  private final HttpClient httpClient;
  private final ModuleService moduleService;
  private final ModuleBuildService moduleBuildService;

  @Inject
  public BlazarConfigurationControlledBadBuildClient (ModuleService moduleService,
                                                      ModuleBuildService moduleBuildService,
                                                      BlazarConfiguration blazarConfiguration,
                                                      HttpClient httpClient) {
    this.moduleService = moduleService;
    this.moduleBuildService = moduleBuildService;

    if (!blazarConfiguration.getBadBuildServiceConfiguration().isPresent()) {
      throw new IllegalArgumentException("Cannot provide BadBuildClient without configuration");
    }

    BadBuildServiceConfiguration conf = blazarConfiguration.getBadBuildServiceConfiguration().get();
    this.getBadBuildsByBuildNameEndpoint = conf.getGetBadBuildsByBuildNameEndpoint();
    this.markBuildAsBadEndpoint = conf.getMarkBuildAsBadEndpoint();
    this.unMarkBuildAsBadEndpoint = conf.getUnMarkBuildAsBadEndpoint();
    this.httpClient = httpClient;
  }

  @Override
  public Set<BadBuild> getBadBuildsByModuleId(int moduleId) {
    String buildName = moduleService.getModuleCoordinateById(moduleId);
    HttpRequest httpRequest = HttpRequest.newBuilder()
        .setMethod(HttpRequest.Method.GET)
        .setUrl(getBadBuildsByBuildNameEndpoint + "/" + buildName)
        .build();
    return httpClient.execute(httpRequest)
        .getAs(new TypeReference<Set<BadBuild>>(){});
  }

  @Override
  public void markBuildAsBad(long moduleBuildId) {
    ModuleBuild moduleBuild = moduleBuildService.getWithId(moduleBuildId);
    String buildName = moduleService.getModuleCoordinateById(moduleBuild.getModuleId());
    HttpRequest.Method method = HttpRequest.Method.PUT;
    httpClient.execute(makeRequest(markBuildAsBadEndpoint, method, buildName, moduleBuild.getBuildNumber()));
  }

  @Override
  public void unMarkBuildAsBad(long moduleBuildId) {
    ModuleBuild moduleBuild = moduleBuildService.getWithId(moduleBuildId);
    String buildName = moduleService.getModuleCoordinateById(moduleBuild.getModuleId());
    HttpRequest.Method method = HttpRequest.Method.DELETE;
    httpClient.execute(makeRequest(unMarkBuildAsBadEndpoint, method, buildName, moduleBuild.getBuildNumber()));
  }

  HttpRequest makeRequest(String endpoint, HttpRequest.Method method, String buildName, int buildNumber) {
    return HttpRequest.newBuilder()
        .setMethod(method)
        .setUrl(endpoint)
        .setBody(new BadBuild(buildName, buildNumber))
        .build();

  }
}
