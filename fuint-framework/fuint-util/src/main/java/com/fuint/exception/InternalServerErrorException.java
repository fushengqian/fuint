package com.fuint.exception;

/**
 * Exception for internal server error (e.g. i/o exception)
 *
 * @author Harrison han
 */
public class InternalServerErrorException extends HttpStatusException {

  private static final Integer STATUS_CODE = 500;

  public InternalServerErrorException() {

    super.setStatus(STATUS_CODE);
    super.setMessage("internal server error");

  }

  public InternalServerErrorException(String message) {

    super.setStatus(STATUS_CODE);
    super.setMessage(message);

  }

  public InternalServerErrorException(String message, Exception cause) {

    super(cause);
    super.setStatus(STATUS_CODE);
    super.setMessage(message);

  }

}
