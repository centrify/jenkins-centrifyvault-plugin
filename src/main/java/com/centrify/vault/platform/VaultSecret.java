package com.centrify.vault.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VaultSecret extends VaultObject {
    public final String apiRetrieveSecret = "/ServerManage/RetrieveSecretContents";
    private String secretName;
    private String type;
    private String parentPath;

    @JsonProperty("SecretName")
    public String getSecretName() {
        return this.secretName;
    }

    public void setSecretName(String name) {
        this.secretName = name;
    }

    @JsonProperty("Type")
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("ParentPath")
    public String getParentPath() {
        return this.parentPath;
    }

    public void setParentPath(String path) {
        this.parentPath = path;
    }

    public String getQueryStatement() {
        String query = "SELECT * FROM DataVault WHERE 1=1";
        if (secretName != null && !secretName.isEmpty()) {
            query += " AND SecretName='" + secretName + "'";
        }
        query += " AND ParentPath='" + parentPath + "'";

        return query;
    }
}
