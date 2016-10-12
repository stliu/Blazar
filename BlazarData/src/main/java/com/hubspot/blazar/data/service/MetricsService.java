package com.hubspot.blazar.data.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.hubspot.blazar.base.InterProjectBuild;
import com.hubspot.blazar.base.ModuleBuild;
import com.hubspot.blazar.base.RepositoryBuild;
import com.hubspot.blazar.base.metrics.StateToActiveBuildCountPair;
import com.hubspot.blazar.data.dao.MetricsDao;

public class MetricsService {

  private MetricsDao dao;

  @Inject
  public MetricsService(MetricsDao dao) {
    this.dao = dao;
  }

  public Map<ModuleBuild.State, Integer> countActiveModuleBuildsByState() {
    Set<StateToActiveBuildCountPair<ModuleBuild.State>> pairs = dao.countActiveModuleBuildsByState();
    Map<ModuleBuild.State, Integer> stateCountMap = new HashMap<>();
    for (StateToActiveBuildCountPair<ModuleBuild.State> pair : pairs) {
      stateCountMap.put(pair.getState(), pair.getCount());
    }
    return stateCountMap;
  }

  public Map<RepositoryBuild.State, Integer> countActiveBranchBuildsByState() {
    Set<StateToActiveBuildCountPair<RepositoryBuild.State>> pairs = dao.countActiveBranchBuildsByState();
    Map<RepositoryBuild.State, Integer> stateCountMap = new HashMap<>();
    for (StateToActiveBuildCountPair<RepositoryBuild.State> pair : pairs) {
      stateCountMap.put(pair.getState(), pair.getCount());
    }
    return stateCountMap;
  }

  public Map<InterProjectBuild.State, Integer> countActiveInterProjectBuildsByState() {
    Set<StateToActiveBuildCountPair<InterProjectBuild.State>> pairs = dao.countActiveInterProjectBuildsByState();
    Map<InterProjectBuild.State, Integer> stateCountMap = new HashMap<>();
    for (StateToActiveBuildCountPair<InterProjectBuild.State> pair : pairs) {
      stateCountMap.put(pair.getState(), pair.getCount());
    }
    return stateCountMap;
  }
}
