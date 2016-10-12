package com.hubspot.blazar.data.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.hubspot.blazar.base.InterProjectBuild;
import com.hubspot.blazar.base.ModuleBuild;
import com.hubspot.blazar.base.RepositoryBuild;

public class CachingMetricsService {
  private static final Logger LOG = LoggerFactory.getLogger(CachingMetricsService.class);
  private static final long MODULE_BUILD_COUNT_MAX_AGE_MILLIS = 500;
  private static final long BRANCH_BUILD_COUNT_MAX_AGE_MILLIS = 500;
  private static final long INTER_PROJECT_BUILD_COUNT_MAX_AGE_MILLIS = 500;
  private final MetricsService metricsService;
  private volatile Map<ModuleBuild.State, Integer> moduleBuildCountMap = new HashMap<>();
  private volatile Map<RepositoryBuild.State, Integer> branchBuildCountMap = new HashMap<>();
  private volatile Map<InterProjectBuild.State, Integer> interProjectBuildCountMap = new HashMap<>();
  private volatile long moduleBuildCountMapLastWrite = 0;
  private volatile long branchBuildCountMapLastWrite = 0;
  private volatile long interProjectBuildCountMapLastWrite = 0;

  @Inject
  public CachingMetricsService(MetricsService metricsService) {
    this.metricsService = metricsService;
  }


  public synchronized int getCachedActiveModuleBuildCountByState(ModuleBuild.State state) {
    return doUpdateAndGetValue("Active BranchBuild Counts",
        state,
        moduleBuildCountMap,
        moduleBuildCountMapLastWrite,
        MODULE_BUILD_COUNT_MAX_AGE_MILLIS,
        metricsService::countActiveModuleBuildsByState);
  }

  public synchronized int getCachedActiveBranchBuildCountByState(RepositoryBuild.State state) {
    return doUpdateAndGetValue("Active BranchBuild Counts",
        state,
        branchBuildCountMap,
        branchBuildCountMapLastWrite,
        BRANCH_BUILD_COUNT_MAX_AGE_MILLIS,
        metricsService::countActiveBranchBuildsByState);
  }

  public synchronized int getCachedActiveInterProjectBuildCountByState(InterProjectBuild.State state) {
    return doUpdateAndGetValue("Active InterProjectBuild Counts",
        state,
        interProjectBuildCountMap,
        interProjectBuildCountMapLastWrite,
        INTER_PROJECT_BUILD_COUNT_MAX_AGE_MILLIS,
        metricsService::countActiveInterProjectBuildsByState);
  }

  /**
   * @param name The cache we're fetching from (for logging)
   * @param state The state we're trying to get the value of
   * @param cachedMap The map we're storing the values in
   * @param lastWrite The last time that map was written to
   * @param maxAge THe max age of the map before we re-fetch all the values
   * @param cacheFetchFunction The function to get all the values from
   * @param <T> The state we're fetching
   * @return The number of builds in that state
   */
  private static <T extends Enum<T>> int doUpdateAndGetValue(String name,
                                                             T state,
                                                             Map<T, Integer> cachedMap,
                                                             long lastWrite,
                                                             long maxAge,
                                                             Supplier<Map<T, Integer>> cacheFetchFunction) {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastWrite > maxAge) {
      LOG.info("Updating cache for {}", name);
      cachedMap = cacheFetchFunction.get();
      lastWrite = currentTime;
    }

    if (!cachedMap.containsKey(state)) {
      throw new RuntimeException(String.format("No such state: %s", state.name()));
    }

    Integer value = cachedMap.get(state);
    if (value == null) {
      throw new RuntimeException(String.format("Found null for count of builds in state %s", state.name()));
    }
    return value;
  }

}
