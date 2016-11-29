package com.hubspot.blazar.externalservice.badbuilds;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Test;
import org.mockito.Matchers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.hubspot.blazar.base.ModuleBuild;
import com.hubspot.blazar.config.BadBuildServiceConfiguration;
import com.hubspot.blazar.config.BlazarConfiguration;
import com.hubspot.blazar.data.service.ModuleBuildService;
import com.hubspot.blazar.data.service.ModuleService;
import com.hubspot.horizon.HttpClient;
import com.hubspot.horizon.HttpRequest;
import com.hubspot.horizon.HttpResponse;

public class BlazarConfigurationControlledBadBuildClientTest {
  private static final String GET_BAD_BUILDS_BY_BUILD_NAME_ENDPOINT = "http://example.com/get-bad-builds-by-build-name-endpoint";
  private static final String MARK_BUILD_AS_BAD_ENDPOINT = "http://example.com/mark-build-as-bad-endpoint";
  private static final String UN_MARK_BUILD_AS_BAD_ENDPOINT = "http://example.com/un-mark-build-as-bad-endpoint";
  private static final BadBuildServiceConfiguration BAD_BUILD_SERVICE_CONFIGURATION = new BadBuildServiceConfiguration(GET_BAD_BUILDS_BY_BUILD_NAME_ENDPOINT, MARK_BUILD_AS_BAD_ENDPOINT, UN_MARK_BUILD_AS_BAD_ENDPOINT);
  private static final ModuleService MODULE_SERVICE = mock(ModuleService.class);
  private static final ModuleBuildService MODULE_BUILD_SERVICE = mock(ModuleBuildService.class);
  private static final BlazarConfiguration BLAZAR_CONFIGURATION = mock(BlazarConfiguration.class);
  private static final HttpClient HTTP_CLIENT = mock(HttpClient.class);
  private static final ModuleBuild MODULE_BUILD = mock(ModuleBuild.class);
  private static final String MODULE_COORDINATE = "some-test-coordinate";

  @Test
  public void testThatItThrowsExceptionWhenConstructedWithoutProperConfiguration() {
    String expectedMessage = "Cannot provide BadBuildClient without configuration";
    when(BLAZAR_CONFIGURATION.getBadBuildServiceConfiguration()).thenReturn(Optional.absent());
    BlazarConfigurationControlledBadBuildClient client = null;
    try {
      client = new BlazarConfigurationControlledBadBuildClient(MODULE_SERVICE, MODULE_BUILD_SERVICE, BLAZAR_CONFIGURATION, HTTP_CLIENT);
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo(expectedMessage);
    }
    assertThat(client).isEqualTo(null);
  }

  @Test
  public void testThatItMakesRequestsToTheConfiguredEndpoints() {
    // configuration
    when(BLAZAR_CONFIGURATION.getBadBuildServiceConfiguration()).thenReturn(Optional.of(BAD_BUILD_SERVICE_CONFIGURATION));

    // The module build being marked / un-marked
    when(MODULE_BUILD.getId()).thenReturn(Optional.of(1L));
    when(MODULE_BUILD.getModuleId()).thenReturn(1);
    when(MODULE_BUILD.getBuildNumber()).thenReturn(1337);

    //  The service calculates the coordinate
    when(MODULE_SERVICE.getModuleCoordinateById(1)).thenReturn(MODULE_COORDINATE);

    // The service returns our module build
    when(MODULE_BUILD_SERVICE.getWithId(1)).thenReturn(MODULE_BUILD);

    BlazarConfigurationControlledBadBuildClient client =
        spy(new BlazarConfigurationControlledBadBuildClient(MODULE_SERVICE, MODULE_BUILD_SERVICE, BLAZAR_CONFIGURATION, HTTP_CLIENT));

    HttpResponse response = mock(HttpResponse.class);
    // build 10 was marked bad
    doAnswer(invocation -> ImmutableSet.of(new BadBuild(MODULE_COORDINATE, 10)))
        .when(response).getAs(Matchers.any(TypeReference.class));

    // verify that the right endpoint was called
    doAnswer(invocation -> {
          HttpRequest request = (HttpRequest) invocation.getArguments()[0];
          assertThat(request.getMethod()).isEqualTo(HttpRequest.Method.GET);
          assertThat(request.getUrl().toString()).isEqualTo(GET_BAD_BUILDS_BY_BUILD_NAME_ENDPOINT + "/" + MODULE_COORDINATE);
          return response;
        }).when(HTTP_CLIENT).execute(any());
    Set<BadBuild> badBuilds = client.getBadBuildsByModuleId(1);
    assertThat(badBuilds).isEqualTo(ImmutableSet.of(new BadBuild(MODULE_COORDINATE, 10)));


    when(HTTP_CLIENT.execute(any())).thenReturn(null);

    doAnswer(invocation -> {
      String endpoint = (String) invocation.getArguments()[0];
      HttpRequest.Method method = (HttpRequest.Method) invocation.getArguments()[1];
      String buildName = (String) invocation.getArguments()[2];
      int buildNumber = (int) invocation.getArguments()[3];

      assertThat(method).isEqualTo(HttpRequest.Method.PUT);
      assertThat(endpoint).isEqualTo(MARK_BUILD_AS_BAD_ENDPOINT);
      assertThat(buildName).isEqualTo(MODULE_COORDINATE);
      assertThat(buildNumber).isEqualTo(1337);
      return null;
    }).when(client).makeRequest(any(), any(), any(), any());
    client.markBuildAsBad(1);

    doAnswer(invocation -> {
      String endpoint = (String) invocation.getArguments()[0];
      HttpRequest.Method method = (HttpRequest.Method) invocation.getArguments()[1];
      String buildName = (String) invocation.getArguments()[2];
      int buildNumber = (int) invocation.getArguments()[3];

      assertThat(method).isEqualTo(HttpRequest.Method.DELETE);
      assertThat(endpoint).isEqualTo(UN_MARK_BUILD_AS_BAD_ENDPOINT);
      assertThat(buildName).isEqualTo(MODULE_COORDINATE);
      assertThat(buildNumber).isEqualTo(1337);
      return null;
    }).when(client).makeRequest(any(), any(), any(), any());
    client.unMarkBuildAsBad(1);
  }
}
