package com.centrify.vault.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VaultAccount extends VaultObject {
    public final String apiCheckoutPassword = "/ServerManage/CheckoutPassword";
    public final String apiCheckinPassword = "/ServerManage/CheckinPassword";

    private String userName;
    private String hostID;
    private String databaseID;
    private String domainID;
    private String credentialType;
    private String credentialID;

    @JsonProperty("User")
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    @JsonProperty("Host")
    public String getHostID() {
        return this.hostID;
    }

    public void setHostID(String id) {
        this.hostID = id;
    }

    @JsonProperty("DatabaseID")
    public String getDatabaseID() {
        return this.databaseID;
    }

    public void setDatabaseID(String id) {
        this.databaseID = id;
    }

    @JsonProperty("DomainID")
    public String getDomainID() {
        return this.domainID;
    }

    public void setDomainID(String id) {
        this.domainID = id;
    }

    @JsonProperty("CredentialType")
    public String getCredentialType() {
        return this.credentialType;
    }

    public void setCredentialType(String type) {
        this.credentialType = type;
    }

    @JsonProperty("CredentialId")
    public String getCredentialID() {
        return this.credentialID;
    }

    public void setCredentialID(String id) {
        this.credentialID = id;
    }

    public String getQueryStatement() {
        String query = "SELECT * FROM VaultAccount WHERE 1=1";
        if (userName != null && !userName.isEmpty()) {
            query += " AND User='" + userName + "'";
        }
        if (hostID != null && !hostID.isEmpty()) {
            query += " AND Host='" + hostID + "'";
        } else if (databaseID != null && !databaseID.isEmpty()) {
            query += " AND DatabaseID='" + databaseID + "'";
        } else if (domainID != null && !domainID.isEmpty()) {
            query += " AND DomainID='" + domainID + "'";
        }

        return query;
    }
}
