package com.centrify.vault.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VaultDomain extends VaultObject {

    public String getQueryStatement() {
        String query = "SELECT * FROM VaultDomain WHERE 1=1";
        if (this.name != null && !this.name.isEmpty()) {
            query += " AND Name='" + this.name + "'";
        }

        return query;
    }
}
