package com.kingston.koratuwa.utility;

import com.kingston.koratuwa.constants.Constants;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

public class ResponseWrapper<T> implements Serializable {

    private String message;
    private String timestamp = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime().toString();
    private T data;

    public ResponseWrapper() {
    }

    public ResponseWrapper<T> responseOk(T data) {
        message = Constants.RESPONSE_OK;
        this.data = data;
        return this;
    }

    public ResponseWrapper<T> responseOk(String msg, T data) {
        message = msg;
        this.data = data;
        return this;
    }

    public ResponseWrapper<T> responseFail(T data) {
        message = (String) data;
        this.data = data;
        return this;
    }

    public ResponseWrapper(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ResponseWrapper{"
                + "message='" + message + '\''
                + ", data=" + data
                + '}';
    }
}
