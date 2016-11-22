package com.hubspot.blazar.resources;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.google.inject.Inject;
import com.hubspot.blazar.externalservice.badbuilds.BadBuild;
import com.hubspot.blazar.externalservice.badbuilds.BadBuildClient;

@Path("/modules")
public class ModuleResource {


  private BadBuildClient badBuildClient;

  @Inject
  public  ModuleResource(BadBuildClient badBuildClient) {
    this.badBuildClient = badBuildClient;
  }

  @GET
  @Path("/{moduleId}")
  public Set<BadBuild> getByBuildName(@PathParam("moduleId") int moduleId) {
    return badBuildClient.getBadBuildsByModuleId(moduleId);
  }
}
