package com.hubspot.blazar.base;


import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class Dependency {
  private final String name;
  private String version;

  @JsonCreator
  public Dependency(@JsonProperty("name") String name,
                    @JsonProperty("version") String version) {
    this.name = name;
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  @JsonCreator
  public static Dependency fromString(String fromString) {
    return new Dependency(fromString, "");
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Dependency that = (Dependency) o;
    return Objects.equals(name, that.name) && Objects.equals(version, that.version);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .add("version", version)
        .toString();
  }
}
