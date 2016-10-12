package com.hubspot.blazar.base.metrics;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hubspot.blazar.base.BasicBuildState;

public class StateToActiveBuildCountPair<T extends Enum<T> & BasicBuildState> {

  private final T state;
  private final int count;

  @JsonCreator
  public StateToActiveBuildCountPair(@JsonProperty("state") T state, @JsonProperty("count") int count) {
    this.state = state;
    this.count = count;
  }

  public T getState() {
    return state;
  }

  public int getCount() {
    return count;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    StateToActiveBuildCountPair<T> pair = (StateToActiveBuildCountPair<T>) o;
    return state.equals(pair.state) && count == pair.count;
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, count);
  }
}
