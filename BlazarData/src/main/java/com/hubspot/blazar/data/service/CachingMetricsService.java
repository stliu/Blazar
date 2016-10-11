package com.hubspot.blazar.data.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.hubspot.blazar.base.ModuleBuild;
import com.hubspot.blazar.base.RepositoryBuild;

public class CachingMetricsService {
  private static final Logger LOG = LoggerFactory.getLogger(CachingMetricsService.class);
  private static final long MODULE_BUILD_COUNT_MAX_AGE_MILLIS = 500;
  private static final long BRANCH_BUILD_COUNT_MAX_AGE_MILLIS = 500;
  private final MetricsService metricsService;
  private volatile Map<ModuleBuild.State, Integer> moduleBuildCountMap;
  private volatile Map<RepositoryBuild.State, Integer> repoBuildCountMap;
  private volatile long moduleBuildCountMapLastWrite;
  private volatile long repoBuildCountMapLastWrite;

  @Inject
  public CachingMetricsService(MetricsService metricsService) {
    this.metricsService = metricsService;
    this.moduleBuildCountMap = new HashMap<>();
    this.moduleBuildCountMapLastWrite = 0;
    this.repoBuildCountMapLastWrite = 0;
  }


  public synchronized Integer getCachedActiveModuleBuildCountByState(ModuleBuild.State state) {
    long currentTime = System.currentTimeMillis();
    if (currentTime - moduleBuildCountMapLastWrite > MODULE_BUILD_COUNT_MAX_AGE_MILLIS) {
      // refresh cache
      LOG.info("Refreshing moduleBuildCountMap cache");
      moduleBuildCountMap = metricsService.countActiveModuleBuildsByState();
      moduleBuildCountMapLastWrite = currentTime;
    }

    if (!moduleBuildCountMap.containsKey(state)) {
      return 0;
    }

    return moduleBuildCountMap.get(state);
  }

  public synchronized Integer getCachedActiveBranchBuildCountByState(RepositoryBuild.State state) {
    long currentTime = System.currentTimeMillis();
    if (currentTime - repoBuildCountMapLastWrite > BRANCH_BUILD_COUNT_MAX_AGE_MILLIS) {
      LOG.info("Refreshing branchBuildCountMap cache");
      repoBuildCountMap = metricsService.countActiveBranchBuildsByState();
      repoBuildCountMapLastWrite = currentTime;
    }

    if (!repoBuildCountMap.containsKey(state)) {
      return 0;
    }

    return repoBuildCountMap.get(state);
  }
}
