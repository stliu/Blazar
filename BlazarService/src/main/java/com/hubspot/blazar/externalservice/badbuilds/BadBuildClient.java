package com.hubspot.blazar.externalservice.badbuilds;

import java.util.Set;

import com.google.inject.ImplementedBy;
import com.hubspot.blazar.exception.NotConfiguredException;

@ImplementedBy(BlazarConfigurationControlledBadBuildClient.class)
public interface BadBuildClient {

  Set<BadBuild> getBadBuildsByModuleId(int moduleId);
  void markBuildAsBad(long moduleBuildId);
  void unMarkBuildAsBad(long moduleBuildId);


  /**
   * This class provides a default implementation for a BadBuildClient for guice to inject
   * in the event that there is no configuration for a {@See BlazarConfigurationControlledBadBuildClient } to use
   */
  class DisabledBadBuildClient implements BadBuildClient {

    public static final String message = "No BadBuildServiceConfiguration was provided at service start, this functionality is disabled";

    @Override
    public Set<BadBuild> getBadBuildsByModuleId(int moduleId) {
      throw new NotConfiguredException(message);
    }

    @Override
    public void markBuildAsBad(long moduleBuildId) {
      throw new NotConfiguredException(message);
    }

    @Override
    public void unMarkBuildAsBad(long moduleBuildId) {
      throw new NotConfiguredException(message);
    }
  }
}
