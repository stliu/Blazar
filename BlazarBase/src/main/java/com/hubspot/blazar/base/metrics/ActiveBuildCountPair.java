package com.hubspot.blazar.base.metrics;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveBuildCountPair<T> {

  final T state;
  final int count;

  @JsonCreator
  public ActiveBuildCountPair(@JsonProperty("state") T state,
                              @JsonProperty("count") int count) {
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

    ActiveBuildCountPair<T> pair = (ActiveBuildCountPair<T>) o;
    return state.equals(pair.state) && count == pair.count;
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, count);
  }
}
