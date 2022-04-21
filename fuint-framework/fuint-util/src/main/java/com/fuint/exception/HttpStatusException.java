package com.fuint.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * The base exception wrapper class.
 * structure:
 * <code>
 * {
 *    status: xxx,
 *    message: xxxxxx,
 *    verbose: {
 *      p1: xxx,
 *      p2: xxx
 *    }
 * }
 * </code>
 *
 * @author Harrison han
 */
public class HttpStatusException extends RuntimeException {

  private int status = 500;

  private Map verbose;

  /**
   * default message
   */
  private String message = "internal server error";

  private String msgTplt;

  private Object[] args;

  public HttpStatusException() {}

  public HttpStatusException(Exception cause) {
    super(cause);
  }

  public Map toSerializableMap() {

    Map<String, Object> serializableMap = new HashMap<String, Object>();
    serializableMap.put("status", this.status);
    serializableMap.put("message", this.message);

    if (this.verbose != null) {
      serializableMap.put("verbose", this.verbose);
    }

    return serializableMap;

  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Map getVerbose() {
    return verbose;
  }

  public void setVerbose(Map verbose) {
    this.verbose = verbose;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMsgTplt() {
    return msgTplt;
  }

  public void setMsgTplt(String msgTplt) {
    this.msgTplt = msgTplt;
  }

  public Object[] getArgs() {
    return args;
  }

  public void setArgs(Object[] args) {
    this.args = args;
  }
}
