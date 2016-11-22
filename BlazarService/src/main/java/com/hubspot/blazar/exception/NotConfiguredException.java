package com.hubspot.blazar.exception;

import javax.ws.rs.WebApplicationException;

public class NotConfiguredException extends WebApplicationException {

  public NotConfiguredException(String message) {
    super(message);
  }
}
