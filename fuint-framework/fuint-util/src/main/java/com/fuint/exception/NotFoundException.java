package com.fuint.exception;

/**
 * Exception for resource not found.
 *
 * This exception always been thrown on a missing resource finding with specified identifier
 *
 * @author Harrison han
 */
public class NotFoundException extends HttpStatusException {

  private static final Integer STATUS_CODE = 404;

  public NotFoundException() {

    super.setStatus(STATUS_CODE);
    super.setMessage("not found");

  }

  public NotFoundException(String message) {

    super.setStatus(STATUS_CODE);
    super.setMessage(message);

  }

  public NotFoundException(String message, Exception cause) {

    super(cause);
    super.setStatus(STATUS_CODE);
    super.setMessage(message);

  }

}
