package com.hubspot.blazar.resources;

import com.google.common.base.Optional;
import com.hubspot.blazar.base.GitInfo;
import com.hubspot.blazar.base.Module;
import com.hubspot.blazar.data.service.BranchService;
import com.hubspot.blazar.data.service.ModuleService;
import com.hubspot.blazar.discovery.ModuleDiscovery;
import com.hubspot.jackson.jaxrs.PropertyFiltering;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Set;

@Path("/branches")
@Produces(MediaType.APPLICATION_JSON)
public class BranchResource {
  private final BranchService branchService;
  private final ModuleService moduleService;
  private final ModuleDiscovery moduleDiscovery;

  @Inject
  public BranchResource(BranchService branchService, ModuleService moduleService, ModuleDiscovery moduleDiscovery) {
    this.branchService = branchService;
    this.moduleService = moduleService;
    this.moduleDiscovery = moduleDiscovery;
  }

  @GET
  @PropertyFiltering
  public Set<GitInfo> getAll() {
    return branchService.getAll();
  }

  @GET
  @Path("/{id}")
  @PropertyFiltering
  public Optional<GitInfo> get(@PathParam("id") int branchId) {
    return branchService.get(branchId);
  }

  @GET
  @Path("/{id}/modules")
  @PropertyFiltering
  public Set<Module> getModules(@PathParam("id") int branchId) {
    return moduleService.getByBranch(branchId);
  }

  @POST
  @Path("/{id}/discover")
  @PropertyFiltering
  public Set<Module> discoverModules(@PathParam("id") int branchId) throws IOException {
    GitInfo gitInfo = get(branchId).get();
    return moduleService.setModules(gitInfo, moduleDiscovery.discover(gitInfo));
  }
}
