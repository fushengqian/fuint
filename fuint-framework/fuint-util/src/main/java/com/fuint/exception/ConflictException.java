package com.fuint.exception;

/**
 * Exception for modifying resource on an unexpected status
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public class ConflictException extends HttpStatusException {

  private static final Integer STATUS_CODE = 409;

  public ConflictException() {

    super.setStatus(STATUS_CODE);
    super.setMessage("conflict");

  }

  public ConflictException(String message) {

    super.setStatus(STATUS_CODE);
    super.setMessage(message);

  }

  public ConflictException(String message, Exception cause) {

    super(cause);
    super.setStatus(STATUS_CODE);
    super.setMessage(message);

  }

}
