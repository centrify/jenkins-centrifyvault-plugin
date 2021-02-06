package com.centrify.vault.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VaultSystem extends VaultObject {
    private String fqdn;
    private String computerClass;

    @JsonProperty("FQDN")
    public String getFQDN() {
        return this.fqdn;
    }

    public void setFQDN(String fqdn) {
        this.fqdn = fqdn;
    }

    @JsonProperty("ComputerClass")
    public String getComputerClass() {
        return this.computerClass;
    }

    public void setComputerClass(String type) {
        this.computerClass = type;
    }

    public String getQueryStatement() {
        String query = "SELECT * FROM Server WHERE 1=1";
        if (this.name != null && !this.name.isEmpty()) {
            query += " AND Name='" + this.name + "'";
        }

        return query;
    }
}
