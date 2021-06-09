package com.fuint.exception;

public class BusinessRuntimeException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Throwable         rootCause;

    public BusinessRuntimeException(String arg0) {
        super(arg0);
        this.errorKey = arg0;
        rootCause = this;
    }

    public BusinessRuntimeException() {
        super();
    }

    public BusinessRuntimeException(String s, Throwable e) {
        this(s);
        if (e instanceof BusinessRuntimeException) {
            rootCause = ((BusinessRuntimeException) e).rootCause;
        } else {
            rootCause = e;
        }
    }

    public BusinessRuntimeException(Throwable e) {
        this("", e);
    }

    public Throwable getRootCause() {
        return rootCause;
    }

    private String errorKey;

    public String getErrorKey() {
        return errorKey;
    }

    private String[] errorParam;

    private Object[] errorObjectParam;

    public Object[] getErrorObjectParam() {
        return errorObjectParam;
    }

    public void setErrorObjectParam(Object[] errorObjectParam) {
        this.errorObjectParam = errorObjectParam;
    }

    public BusinessRuntimeException(String key, Object[] objectParam) {
        this(key);
        this.errorObjectParam = objectParam;
    }

    public String[] getErrorParam() {
        return errorParam;
    }

    public void setErrorParam(String[] errorParam) {
        this.errorParam = errorParam;
    }
}
