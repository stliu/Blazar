
package com.hubspot.blazar.data.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.hubspot.blazar.base.ModuleState;
import com.hubspot.blazar.data.BlazarDataTestBase;

public class StateServiceTest extends BlazarDataTestBase {
  private StateService stateService;

  @Before
  public void before() throws Exception {
    runSql("StateServiceTest1.sql");
    this.stateService = getFromGuice(StateService.class);
  }

  @Test
  public void testThatStateServiceGetsCorrectLastBuild() {
    Set<ModuleState> states = stateService.getModuleStatesByBranch(1);
    for (ModuleState s : states) {
      if (s.getLastRepoBuild().isPresent()) {
        assertThat(s.getLastRepoBuild().get().getBranchId()).isEqualTo(1);
        assertThat(s.getLastRepoBuild().get().getBuildNumber()).isEqualTo(1);
      } else {
        fail("No last repo build found");
      }
    }
  }
}
