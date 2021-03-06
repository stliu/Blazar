package com.hubspot.blazar.data.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;

import com.google.common.base.Optional;
import com.hubspot.blazar.base.InterProjectBuild;
import com.hubspot.rosetta.jdbi.BindWithRosetta;

public interface InterProjectBuildDao {

  @SingleValueResult
  @SqlQuery("SELECT * FROM inter_project_builds WHERE id = :id")
  Optional<InterProjectBuild> getWithId(@Bind("id") long id);

  @GetGeneratedKeys
  @SqlUpdate("INSERT INTO inter_project_builds (state, moduleIds, startTimestamp, buildTrigger) VALUES (:state, :moduleIds, :startTimestamp, :buildTrigger)")
  int enqueue(@BindWithRosetta InterProjectBuild interProjectBuild);

  @SqlUpdate("UPDATE inter_project_builds SET " +
             "state = :state, " +
             "moduleIds = :moduleIds, " +
             "dependencyGraph = :dependencyGraph " +
             "WHERE id = :id and state in ('QUEUED')")
  void start(@BindWithRosetta InterProjectBuild interProjectBuild);

  @SqlUpdate("UPDATE inter_project_builds SET " +
             "state = :state, " +
             "endTimestamp = :endTimestamp " +
             "WHERE id = :id and state in ('IN_PROGRESS')")
  void finish(@BindWithRosetta InterProjectBuild interProjectBuild);
}
