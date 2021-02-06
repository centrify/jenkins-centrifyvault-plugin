package com.centrify.vault.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VaultDatabase extends VaultObject {
    private String fqdn;
    private String databaseClass;
    private String instanceName;

    @JsonProperty("FQDN")
    public String getFQDN() {
        return this.fqdn;
    }

    public void setFQDN(String fqdn) {
        this.fqdn = fqdn;
    }

    @JsonProperty("DatabaseClass")
    public String getDatabaseClass() {
        return this.databaseClass;
    }

    public void setDatabaseClass(String type) {
        this.databaseClass = type;
    }

    @JsonProperty("InstanceName")
    public String getInstanceName() {
        return this.instanceName;
    }

    public void setInstanceName(String instance) {
        this.instanceName = instance;
    }

    public String getQueryStatement() {
        String query = "SELECT * FROM VaultDatabase WHERE 1=1";
        if (this.name != null && !this.name.isEmpty()) {
            query += " AND Name='" + this.name + "'";
        }
        if (fqdn != null && !fqdn.isEmpty()) {
            query += " AND FQDN='" + fqdn + "'";
        }
        if (databaseClass != null && !databaseClass.isEmpty()) {
            query += " AND DatabaseClass='" + databaseClass + "'";
        }

        return query;
    }
}
