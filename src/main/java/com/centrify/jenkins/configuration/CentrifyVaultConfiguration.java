package com.centrify.jenkins.configuration;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.model.AbstractDescribableImpl;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.verb.POST;
import hudson.Extension;
import jenkins.model.Jenkins;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;

import java.util.Collections;

import com.centrify.jenkins.Messages;
import com.centrify.vault.VaultClient;
import com.centrify.vault.exceptions.CentrifyVaultException;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import static com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials;

public class CentrifyVaultConfiguration extends AbstractDescribableImpl<CentrifyVaultConfiguration> {

    private String vaultURL;
    private String oauthAppID;
    private String oauthScope;
    private String vaultCredentialsId;
    private boolean vaultTrustCert;
    private boolean enableDebugging;

    @DataBoundConstructor
    public CentrifyVaultConfiguration(String vaultURL, String oauthAppID, String oauthScope, String vaultCredentialsId,
            boolean vaultTrustCert, boolean enableDebugging) {
        this.vaultCredentialsId = vaultCredentialsId;
        this.vaultURL = vaultURL;
        this.oauthAppID = oauthAppID;
        this.oauthScope = oauthScope;
        this.vaultTrustCert = vaultTrustCert;
        this.enableDebugging = enableDebugging;
    }

    public String getVaultURL() {
        return this.vaultURL;
    }

    public String getOauthAppID() {
        return this.oauthAppID;
    }

    public String getOauthScope() {
        return this.oauthScope;
    }

    public String getVaultCredentialsId() {
        return this.vaultCredentialsId;
    }

    public boolean getVaultTrustCert() {
        return this.vaultTrustCert;
    }

    public boolean getEnableDebugging() {
        return this.enableDebugging;
    }

    public void setVaultCredentialsId(String vaultCredentialsId) {
        this.vaultCredentialsId = vaultCredentialsId;
    }

    public VaultClient getVaultClient() {
        return CentrifyVaultConfiguration.getVaultClient(getVaultURL(), getOauthAppID(), getOauthScope(),
                getVaultCredentialsId(), getVaultTrustCert(), getEnableDebugging());
    }

    public VaultClient getAuthenticatedVaultClient() throws CentrifyVaultException {
        VaultClient client = getVaultClient();
        client.connectToVault();

        return client;
    }

    public static VaultClient getVaultClient(String tenantURL, String appid, String scope, String credentialsId,
            boolean trustCert, boolean enableDebugging) {
        String username = null;
        String password = null;

        if (credentialsId != null && !credentialsId.isEmpty()) {
            StandardUsernamePasswordCredentials credentials = CredentialsMatchers
                    .firstOrNull(
                            lookupCredentials(StandardUsernamePasswordCredentials.class, (Item) null, ACL.SYSTEM,
                                    Collections.<DomainRequirement>emptyList()),
                            CredentialsMatchers.withId(credentialsId));
            if (credentials != null) {
                username = credentials.getUsername();
                password = credentials.getPassword().getPlainText();
            }
        }

        VaultClient client = new VaultClient(tenantURL, appid, scope, username, password, enableDebugging);

        return client;
    }

    @Extension
    public static class CentrifyVaultConfigurationDescriptor extends Descriptor<CentrifyVaultConfiguration> {

        @POST
        public FormValidation doTestCentrifyVaultConnection(@QueryParameter("vaultURL") final String vaultURL,
                @QueryParameter("oauthAppID") final String oauthAppID,
                @QueryParameter("oauthScope") final String oauthScope,
                @QueryParameter("vaultCredentialsId") final String vaultCredentialsId,
                @QueryParameter("vaultTrustCert") final boolean vaultTrustCert,
                @QueryParameter("enableDebugging") final boolean enableDebugging) {
            // Also, validate that we are an Administrator
            Jenkins.get().checkPermission(Jenkins.ADMINISTER);

            if (vaultURL.isEmpty()) {
                return FormValidation.error(Messages.CentrifyVaultConfiguration_TenantURLRequired());
            }
            if (oauthAppID.isEmpty()) {
                return FormValidation.error(Messages.CentrifyVaultConfiguration_OAuthAppIDRequired());
            }
            if (oauthScope.isEmpty()) {
                return FormValidation.error(Messages.CentrifyVaultConfiguration_OAuthScopeRequired());
            }
            if (vaultCredentialsId.isEmpty()) {
                return FormValidation.error(Messages.CentrifyVaultConfiguration_CredentialRequired());
            }

            VaultClient client = CentrifyVaultConfiguration.getVaultClient(vaultURL, oauthAppID, oauthScope,
                    vaultCredentialsId, vaultTrustCert, enableDebugging);
            try {
                client.connectToVault();
                return FormValidation.ok(Messages.CentrifyVaultConfiguration_ConnectionSuccess());
            } catch (Exception e) {
                return FormValidation.error(e.getMessage());
            }
        }

        @POST
        public ListBoxModel doFillVaultCredentialsIdItems(@AncestorInPath Item item,
                @QueryParameter String credentialsId) {
            StandardListBoxModel result = new StandardListBoxModel();
            if (item == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
                return result.includeCurrentValue(credentialsId);
            }

            if (item != null && !item.hasPermission(Item.EXTENDED_READ)
                    && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                return result.includeCurrentValue(credentialsId);
            }

            return result.includeEmptyValue().includeAs(ACL.SYSTEM, item, StandardUsernamePasswordCredentials.class,
                    URIRequirementBuilder.fromUri(credentialsId).build()).includeCurrentValue(credentialsId);
        }

        @Override
        public String getDisplayName() {
            return Messages.CentrifyVaultConfiguration_CentrifyVault();
        }

    }
}
