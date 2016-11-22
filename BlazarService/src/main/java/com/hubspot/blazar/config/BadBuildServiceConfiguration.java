package com.hubspot.blazar.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BadBuildServiceConfiguration  {

  private final String getBadBuildsByBuildNameEndpoint;
  private final String markBuildAsBadEndpoint;
  private final String unMarkBuildAsBadEndpoint;

  @JsonCreator
  public BadBuildServiceConfiguration(@JsonProperty("getBadBuildsByBuildNameEndpoint") String getBadBuildsByBuildNameEndpoint,
                                      @JsonProperty("markBuildAsBadEndpoint") String markBuildAsBadEndpoint,
                                      @JsonProperty("unMarkBuildAsBadEndpoint") String unMarkBuildAsBadEndpoint) {
    this.getBadBuildsByBuildNameEndpoint = getBadBuildsByBuildNameEndpoint;
    this.markBuildAsBadEndpoint = markBuildAsBadEndpoint;
    this.unMarkBuildAsBadEndpoint = unMarkBuildAsBadEndpoint;
  }

  public String getGetBadBuildsByBuildNameEndpoint() {
    return getBadBuildsByBuildNameEndpoint;
  }

  public String getMarkBuildAsBadEndpoint() {
    return markBuildAsBadEndpoint;
  }

  public String getUnMarkBuildAsBadEndpoint() {
    return unMarkBuildAsBadEndpoint;
  }
}
