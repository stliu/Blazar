package com.hubspot.blazar.data.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.hubspot.blazar.base.DiscoveredModule;
import com.hubspot.blazar.base.GitInfo;
import com.hubspot.blazar.base.Module;
import com.hubspot.blazar.data.dao.ModuleDao;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModuleService {
  private final ModuleDao moduleDao;
  private final DependenciesService dependenciesService;

  @Inject
  public ModuleService(ModuleDao moduleDao, DependenciesService dependenciesService) {
    this.moduleDao = moduleDao;
    this.dependenciesService = dependenciesService;
  }

  public Set<Module> getByBranch(int branchId) {
    return moduleDao.getByBranch(branchId);
  }

  @Transactional
  public Set<Module> setModules(GitInfo gitInfo, Set<DiscoveredModule> updatedModules) {
    Set<Module> oldModules = getByBranch(gitInfo.getId().get());
    Set<Module> newModules = new HashSet<>();

    Map<String, DiscoveredModule> updatedModulesByName = mapByName(updatedModules);
    Map<String, Module> oldModulesByName = mapByName(oldModules);

    for (String deletedModule : Sets.difference(oldModulesByName.keySet(), updatedModulesByName.keySet())) {
      Module module = oldModulesByName.get(deletedModule);
      checkAffectedRowCount(moduleDao.delete(module.getId().get()));
      dependenciesService.delete(module.getId().get());
    }

    for (String updatedModule : Sets.intersection(oldModulesByName.keySet(), updatedModulesByName.keySet())) {
      Module old = oldModulesByName.get(updatedModule);
      DiscoveredModule updated = updatedModulesByName.get(updatedModule).withId(old.getId().get());
      if (!old.equals(updated)) {
        checkAffectedRowCount(moduleDao.update(updated));
      }
      dependenciesService.update(updated);
      newModules.add(updated);
    }

    for (String addedModule : Sets.difference(updatedModulesByName.keySet(), oldModulesByName.keySet())) {
      DiscoveredModule added = updatedModulesByName.get(addedModule);
      int id = moduleDao.insert(gitInfo.getId().get(), added);
      added = added.withId(id);
      dependenciesService.insert(added);
      newModules.add(added);
    }

    return newModules;
  }

  private static void checkAffectedRowCount(int affectedRows) {
    Preconditions.checkState(affectedRows == 1, "Expected to update 1 row but updated %s", affectedRows);
  }

  private static <T extends Module> Map<String, T> mapByName(Set<T> modules) {
    Map<String, T> modulesByName = new HashMap<>();
    for (T module : modules) {
      modulesByName.put(module.getName().toLowerCase(), module);
    }

    return modulesByName;
  }
}
