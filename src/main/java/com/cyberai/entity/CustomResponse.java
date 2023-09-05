package com.cyberai.entity;

public class CustomResponse {
    public Integer status;
    public String body;

    public Integer getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
