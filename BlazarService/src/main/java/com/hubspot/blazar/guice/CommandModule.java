package com.hubspot.blazar.guice;

import java.util.Map;

import org.kohsuke.github.GitHub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.hubspot.blazar.config.BlazarConfiguration;
import com.hubspot.blazar.config.GitHubConfiguration;
import com.hubspot.blazar.data.BlazarDataModule;
import com.hubspot.blazar.discovery.DiscoveryModule;
import com.hubspot.blazar.util.GitHubHelper;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;

public class CommandModule extends AbstractModule {

  private final Bootstrap<BlazarConfiguration> bootstrap;
  private final BlazarConfiguration configuration;

  public CommandModule(Bootstrap<BlazarConfiguration> bootstrap,
                                        BlazarConfiguration configuration) {
    this.bootstrap = bootstrap;
    this.configuration = configuration;
  }


  @Override
  protected void configure() {
    bootstrap.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    bootstrap.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    bootstrap.getObjectMapper().registerModule(new ProtobufModule());
    binder().bind(ObjectMapper.class).toInstance(bootstrap.getObjectMapper());
    binder().bind(DataSourceFactory.class).toInstance(configuration.getDatabaseConfiguration());
    binder().install(new BlazarDataModule());
    binder().install(new DiscoveryModule());
    binder().bind(GitHubHelper.class);

    MapBinder<String, GitHub> mapBinder = MapBinder.newMapBinder(binder(), String.class, GitHub.class);
    for (Map.Entry<String, GitHubConfiguration> entry : configuration.getGitHubConfiguration().entrySet()) {
      String host = entry.getKey();
      mapBinder.addBinding(host).toInstance(BlazarServiceModule.toGitHub(host, entry.getValue()));
    }
  }
}
