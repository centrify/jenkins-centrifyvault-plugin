package com.centrify.jenkins;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class CentrifyVaultSecret extends AbstractDescribableImpl<CentrifyVaultSecret> {
    private String resourceType;
    private String resourceName;
    private String userName;
    private String envVar;
    
    @DataBoundConstructor
    public CentrifyVaultSecret(String resourceType, String resourceName, String userName, String envVar) {
        this.resourceType = resourceType;
        this.resourceName = resourceName;
        this.userName = userName;
        this.envVar = envVar;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEnvVar() {
        return this.envVar;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<CentrifyVaultSecret> {

        @Override
        public String getDisplayName() {
            return "Vault Secret";
        }

    }
}

