package com.fuint.exception;

/**
 * Exception for unauthorized.
 *
 * This exception always been thrown on authentication error
 *
 * @author Harrison han
 */
public class UnauthorizedException extends HttpStatusException {

  private static final Integer STATUS_CODE = 401;

  public UnauthorizedException() {

    super.setStatus(STATUS_CODE);
    super.setMessage("unauthorized");

  }

  public UnauthorizedException(String message) {

    super.setStatus(STATUS_CODE);
    super.setMessage(message);

  }

  public UnauthorizedException(String message, Exception cause) {

    super(cause);
    super.setStatus(STATUS_CODE);
    super.setMessage(message);

  }

}
