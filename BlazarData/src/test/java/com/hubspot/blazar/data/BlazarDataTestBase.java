package com.hubspot.blazar.data;

import java.util.Map;

import org.junit.After;
import org.junit.BeforeClass;
import org.skife.jdbi.v2.Query;

import com.google.inject.Guice;
import com.hubspot.blazar.test.base.service.BlazarTestBase;

public class BlazarDataTestBase extends BlazarTestBase {

  @BeforeClass
  public static void setup() throws Exception {
    synchronized (injector) {
      if (injector.get() == null) {
        injector.set(Guice.createInjector(new BlazarDataTestModule()));
        runSql("schema.sql");
      }
    }
  }

  @After
  public void cleanup() throws Exception {
    runSql("schema.sql");
  }

   public Query<Map<String, Object>> runRawSql(String query) {
    return getFromGuice(org.skife.jdbi.v2.DBI.class).open().createQuery(query);
  }
}
