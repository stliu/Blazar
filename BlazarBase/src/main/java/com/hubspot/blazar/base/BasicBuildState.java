package com.hubspot.blazar.base;

public interface BasicBuildState {
  enum SimpleState { WAITING, RUNNING, COMPLETE }
  boolean isWaiting();
  boolean isRunning();
  boolean isComplete();
}
