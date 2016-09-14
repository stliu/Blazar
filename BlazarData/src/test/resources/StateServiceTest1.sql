--liquibase formatted sql
--changeset StateServiceTest:0 runAlways:true
TRUNCATE TABLE `inter_project_build_mappings`;
TRUNCATE TABLE `inter_project_builds`;
TRUNCATE TABLE `branches`;
TRUNCATE TABLE `modules`;
TRUNCATE TABLE `module_depends`;
TRUNCATE TABLE `module_provides`;
TRUNCATE TABLE `repo_builds`;
TRUNCATE TABLE `module_builds`;
TRUNCATE TABLE `malformed_files`;
TRUNCATE TABLE `instant_message_configs`;

--changeset StateServiceTest:1 runAlways:true
INSERT INTO `branches` (`id`, `host`, `organization`, `repository`, `repositoryId`, `branch`, `active`, `pendingBuildId`, `inProgressBuildId`, `lastBuildId`, `createdTimestamp`, `updatedTimestamp`)
VALUES (1, 'git.example.com', 'test', 'testRepo', 100, 'master', 1, NULL, NULL, 1, '2016-04-06 13:16:09', '2016-04-06 13:16:09');

INSERT INTO `modules` (`id`, `branchId`, `name`, `type`, `path`, `glob`, `active`, `pendingBuildId`, `inProgressBuildId`, `lastBuildId`, `createdTimestamp`, `updatedTimestamp`)
VALUES (1, 1, 'module1', 'config', 'Module1/.blazar.yaml', 'Module1/*', 1, NULL, NULL, 1, '2016-04-06 13:16:09', '2016-04-06 13:16:09');

-- INSERT INTO modules ("id", "branchId", "name", "type", "path", "glob", "active", "pendingBuildId", "inProgressBuildId", "lastBuildId")
-- VALUES (2, 1, 'module2', 'config', 'Module2/.blazar.yaml', 'Module2/*', 1, NULL, NULL, NULL);

-- a completed repo build
INSERT INTO `repo_builds` (`id`, `branchId`, `buildNumber`, `state`, `startTimestamp`, `endTimestamp`, `commitInfo`, `dependencyGraph`, `sha`, `buildTrigger`, `buildOptions`)
VALUES (1, 1, 1, 'SUCCEEDED', 1473789633161, 1473790018418, '{"fake-data": "Nothing should use this"}', '{"transitiveReduction":{"1":[]},"topologicalSort":[1]}', '4c7dc3455564f19219f8b555c32d7da51d5fc788','{"type":"MANUAL","id":"test"}',
'{"moduleIds":[],"buildDownstreams":"WITHIN_REPOSITORY","resetCaches":false}');

-- associated Module Build
INSERT INTO `module_builds` (`id`, `moduleId`, `repoBuildId`, `buildNumber`, `state`, `startTimestamp`, `endTimestamp`, `buildConfig`, `resolvedConfig`, `taskId`)
VALUES (1, 1, 1, 1, 'SUCCEEDED', 1473789633161, 1473790018418, '{}', '{}', 'test-task-id-1');

