package com.hubspot.blazar.externalservice.badbuilds;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.hubspot.blazar.exception.NotConfiguredException;

public class DisabledBadBuildClientTest {

  @Test
  public void testThatDisabledBadBuildClientThrowsNotConfiguredExceptions() {
    BadBuildClient.DisabledBadBuildClient client = new BadBuildClient.DisabledBadBuildClient();

    List<String> caught = new ArrayList<>();

    try {
      client.getBadBuildsByModuleId(1);
    } catch (NotConfiguredException e) {
      caught.add("getBadBuildsByModuleId");
    }

    try {
      client.markBuildAsBad(1);
    } catch (NotConfiguredException e) {
      caught.add("markBuildAsBad");
    }

    try {
      client.unMarkBuildAsBad(1);
    } catch (NotConfiguredException e) {
      caught.add("unMarkBuildAsBad");
    }

    assertThat(caught).isEqualTo(ImmutableList.of("getBadBuildsByModuleId", "markBuildAsBad", "unMarkBuildAsBad"));
  }
}
