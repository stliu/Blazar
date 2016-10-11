package com.hubspot.blazar.data.dao;

import java.util.Set;

import org.skife.jdbi.v2.sqlobject.SqlQuery;

import com.hubspot.blazar.base.ModuleBuild;
import com.hubspot.blazar.base.RepositoryBuild;
import com.hubspot.blazar.base.metrics.ActiveBuildCountPair;

public interface MetricsDao {

  @SqlQuery("SELECT state, COUNT(id) AS count FROM module_builds " +
      "WHERE state IN ('QUEUED', 'WAITING_FOR_UPSTREAM_BUILD', 'LAUNCHING', 'IN_PROGRESS') GROUP BY state")
  Set<ActiveBuildCountPair<ModuleBuild.State>> countActiveModuleBuildsByState();

  @SqlQuery("SELECT state, COUNT(id) AS count FROM repo_builds " +
      "WHERE state IN ('QUEUED', 'LAUNCHING', 'IN_PROGRESS') GROUP BY state")
  Set<ActiveBuildCountPair<RepositoryBuild.State>> countActiveBranchBuildsByState();

}
