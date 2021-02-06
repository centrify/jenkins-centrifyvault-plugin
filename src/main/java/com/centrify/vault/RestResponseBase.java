package com.centrify.vault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponseBase {
    private Boolean success;
    private String message;
    private Object result;

    @JsonProperty("success")
    public Boolean getSucess() {
        return this.success;
    }

    public void setSucess(Boolean success) {
        this.success = success;
    }

    @JsonProperty("Message")
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    @JsonProperty("Result")
    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
