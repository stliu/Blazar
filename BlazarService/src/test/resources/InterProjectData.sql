--liquibase formatted sql
-- changeset InterProjectTestTruncateTables:0 runAlways:true
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

-- Insert Repo Branches
--changeset InterProjectTestInsertBranches:1 runAlways:true
INSERT INTO `branches` (`id`, `host`, `organization`, `repository`, `repositoryId`, `branch`, `active`, `pendingBuildId`, `inProgressBuildId`, `lastBuildId`, `createdTimestamp`, `updatedTimestamp`) VALUES
  (1,'git.example.com','test','Repo1',1,'master',1,NULL,NULL,NULL,'2016-04-06 13:16:09','2016-04-06 13:16:09'),
  (2,'git.example.com','test','Repo2',2,'master',1,NULL,NULL,NULL,'2016-04-06 13:16:26','2016-04-06 13:16:26'),
  (3,'git.example.com','test','Repo3',3,'master',1,NULL,NULL,NULL,'2016-04-06 13:16:09','2016-04-06 13:16:09'),
  (4,'git.example.com','test','Repo4',4,'master',1,NULL,NULL,NULL,'2016-04-06 13:16:26','2016-04-06 13:16:26'),
  (5,'git.example.com','test','Repo5',5,'master',1,NULL,NULL,NULL,'2016-04-06 13:16:26','2016-04-06 13:16:26');

-- insert modules
--changeset InterProjectTestInsertModules:2 runAlways:true
INSERT INTO `modules` (`id`, `branchId`, `name`, `type`, `path`, `glob`, `active`, `pendingBuildId`, `inProgressBuildId`, `lastBuildId`, `createdTimestamp`, `updatedTimestamp`, `buildpack`) VALUES
-- Repo1 modules
  (1,1, 'Module1','config','/Module1/.blazar.yaml','/Module1/**',1,NULL,NULL,NULL,'2016-04-06 13:17:09','2016-04-06 13:17:09',NULL),
  (2,1, 'Module2','config','/Module2/.blazar.yaml','/Module2/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
  (3,1, 'Module3','config','/Module3/.blazar.yaml','/Module3/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
-- Repo2 modules
  (4,2, 'Module1','config','/Module1/.blazar.yaml','/Module1/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
  (5,2, 'Module2','config','/Module2/.blazar.yaml','/Module2/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
  (6,2, 'Module3','config','/Module3/.blazar.yaml','/Module3/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
-- Repo3 modules
  (7,3, 'Module1','config','/Module1/.blazar.yaml','/Module1/**',1,NULL,NULL,NULL,'2016-04-06 13:18:05','2016-04-06 13:18:05',NULL),
  (8,3, 'Module2','config','/Module2/.blazar.yaml','/Module2/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
  (9,3, 'Module3','config','/Module3/.blazar.yaml','/Module3/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
-- Repo4 modules
  (10,4,'Module1','config','/Module1/.blazar.yaml','/Module1/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
  (11,4,'Module2','config','/Module2/.blazar.yaml','/Module2/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
  (12,4,'Module3','config','/Module3/.blazar.yaml','/Module3/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
-- Repo5 modules
  (13,5,'Module1','config','/Module1/.blazar.yaml','/Module1/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
  (14,5,'Module2','config','/Module2/.blazar.yaml','/Module2/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL),
  (15,5,'Module3','config','/Module3/.blazar.yaml','/Module3/**',1,NULL,NULL,NULL,'2016-04-06 13:17:49','2016-04-06 13:17:49',NULL);

-- insert providers
--changeset InterProjectTestInsertProviders:3 runAlways:true
INSERT INTO `module_depends` (`moduleId`, `name`, `version`) VALUES
/*
  1.1(1)  1.2(2)--+     1.3(3)<-+
   |       |      |       |     |
  2.1(4)  2.2(5)--2.3(6)  |     |
   |       |              |     |
3.1(7)-- 3.2(8)--3.3(9)   |     |
  / \                     |     |
4.1(10) 4.2(11)       4.3(12)   |
 \  /                     |     |
 5.1(13)                  +--->5.2(14)   5.3
 */
-- Repo1
-- 1.1, 1.2 have no deps (root of their trees)
  (3, 'Repo4-Module3', '1.0.0'),  -- circular 1.3 -> 4.3 -> 5.2 -> 1.3
-- Repo2
  (4, 'Repo1-Module1', '1.0.0'), -- 2.1 -> 1.1
  (5, 'Repo1-Module2', '1.0.0'), -- 2.2 -> 1.2
  (6, 'Repo2-Module2', '1.0.0'), -- 2.3 -> 2.2, 1.2 (transitive)
  (6, 'Repo1-Module2', '1.0.0'),
-- Repo3
  (7, 'Repo1-Module1', '1.0.0'), -- 3.1 -> 1.1 (transitive), 2.1
  (7, 'Repo2-Module1', '1.0.0'),
  (8, 'Repo3-Module1', '1.0.0'), -- 3.2 -> 3.1, 2.2.
  (8, 'Repo2-Module2', '1.0.0'),
  (9, 'Repo3-Module2', '1.0.0'), -- 3.3 -> 3.2
-- Repo4
  (10, 'Repo3-Module1', '1.0.0'), -- 4.1 -> 3.1
  (11, 'Repo3-Module1', '1.0.0'), -- 4.2 -> 3.1
  (12, 'Repo5-Module2', '1.0.0'), -- 4.3 -> 5.2 (circular)
-- Repo5
  (13, 'Repo4-Module1', '1.0.0'), -- 5.1 -> 4.1, 4.2  (forms a diamond spanning 3.1 -> 4.1, 4,2 -> 5.1)
  (13, 'Repo4-Module2', '1.0.0'),
  (14, 'Repo1-Module3', '1.0.0');

-- insert dependencies
--changeset InterProjectTestInsertDependencies:4 runAlways:true
INSERT INTO `module_provides` (`moduleId`, `name`, `version`) VALUES
-- Repo1
  ( 1, 'Repo1-Module1', '1.0.0'),
  ( 2, 'Repo1-Module2', '1.0.0'),
  ( 3, 'Repo1-Module3', '1.0.0'),
-- Repo2
  ( 4, 'Repo2-Module1', '1.0.0'),
  ( 5, 'Repo2-Module2', '1.0.0'),
  ( 6, 'Repo2-Module3', '1.0.0'),
-- Repo3
  ( 7, 'Repo3-Module1', '1.0.0'),
  ( 8, 'Repo3-Module2', '1.0.0'),
  ( 9, 'Repo3-Module3', '1.0.0'),
-- Repo4
  (10, 'Repo4-Module1', '1.0.0'),
  (11, 'Repo4-Module2', '1.0.0'),
  (12, 'Repo4-Module3', '1.0.0'),
-- Repo5
  (13, 'Repo5-Module1', '1.0.0'),
  (14, 'Repo5-Module2', '1.0.0'),
  (15, 'Repo5-Module3', '1.0.0');
