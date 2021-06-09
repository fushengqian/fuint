package com.fuint.exception;

/**
 * Exception for accessing unauthenticated resources
 *
 * @author Harrison han
 */
public class ForbiddenException extends HttpStatusException {

  private static final Integer STATUS_CODE = 403;

  public ForbiddenException() {

    super.setStatus(STATUS_CODE);
    super.setMessage("forbidden");

  }

  public ForbiddenException(String message) {

    super.setStatus(STATUS_CODE);
    super.setMessage(message);

  }

  public ForbiddenException(String message, Exception cause) {

    super(cause);
    super.setStatus(STATUS_CODE);
    super.setMessage(message);

  }

}
