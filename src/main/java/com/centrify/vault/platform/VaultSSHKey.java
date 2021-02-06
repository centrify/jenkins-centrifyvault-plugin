package com.centrify.vault.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VaultSSHKey extends VaultObject {
    public final String apiRetrieve = "/ServerManage/RetrieveSshKey";
    private String keyPairType;
    private String passphrase;
    private String keyFormat;

    @JsonProperty("KeyPairType")
    public String getKeyPairType() {
        return this.keyPairType;
    }

    public void setKeyPairType(String type) {
        this.keyPairType = type;
    }

    @JsonProperty("Passphrase")
    public String getPassphrase() {
        return this.passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    @JsonProperty("KeyFormat")
    public String getKeyFormat() {
        return this.keyFormat;
    }

    public void setKeyFormat(String format) {
        this.keyFormat = format;
    }

    public String getQueryStatement() {
        String query = "SELECT * FROM SshKeys WHERE 1=1";
        if (this.name != null && !this.name.isEmpty()) {
            query += " AND Name='" + this.name + "'";
        }

        return query;
    }
}
