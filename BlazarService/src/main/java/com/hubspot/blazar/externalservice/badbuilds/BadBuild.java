package com.hubspot.blazar.externalservice.badbuilds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class BadBuild {

  private String buildName;
  private final int buildNumber;

  @JsonCreator
  public BadBuild(@JsonProperty("buildName") String buildName,
                  @JsonProperty("buildNumber") int buildNumber) {
    this.buildName = buildName;
    this.buildNumber = buildNumber;
  }

  public String getBuildName() {
    return buildName;
  }

  public int getBuildNumber() {
    return buildNumber;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("buildName", buildName)
        .add("buildNumber", buildNumber)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BadBuild badBuild = (BadBuild) o;
    return buildNumber == badBuild.buildNumber &&
        Objects.equal(buildName, badBuild.buildName);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(buildName, buildNumber);
  }
}
