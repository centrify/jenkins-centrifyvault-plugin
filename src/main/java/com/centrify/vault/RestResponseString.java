package com.centrify.vault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RestResponseString extends RestResponseBase {
    private String result;


    @JsonProperty("Result")
    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
