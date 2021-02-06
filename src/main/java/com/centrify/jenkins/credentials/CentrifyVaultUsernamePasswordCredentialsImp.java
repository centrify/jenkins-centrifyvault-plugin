package com.centrify.jenkins.credentials;

import java.io.IOException;

import com.centrify.jenkins.Messages;
import javax.annotation.CheckForNull;

import com.centrify.jenkins.configuration.CentrifyVaultConfiguration;
import com.centrify.jenkins.configuration.CentrifyVaultGlobalConfiguration;
import com.centrify.vault.VaultClient;
import com.centrify.vault.exceptions.CentrifyVaultException;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;

import org.jenkins.ui.icon.Icon;
import org.jenkins.ui.icon.IconSet;
import org.jenkins.ui.icon.IconType;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.Secret;

public class CentrifyVaultUsernamePasswordCredentialsImp extends BaseStandardCredentials
        implements CentrifyVaultUsernamePasswordCredentials {
    private static final long serialVersionUID = 1L;

    private String resourceType;
    private String resourceName;
    private String resourceUserName;

    @DataBoundConstructor
    public CentrifyVaultUsernamePasswordCredentialsImp(@CheckForNull CredentialsScope scope, @CheckForNull String id,
            @CheckForNull String description) {
        super(scope, id, description);
    }

    @Override
    public Secret getSecret() throws IOException, InterruptedException {
        return getPassword();
    }

    @Override
    public String getUsername() {

        return this.resourceUserName;
    }

    @Override
    public Secret getPassword() {
        CentrifyVaultGlobalConfiguration globalConfig = CentrifyVaultGlobalConfiguration.get();
        CentrifyVaultConfiguration config = globalConfig.getCentrifyVaultConfiguration();

        try {
            VaultClient client = config.getAuthenticatedVaultClient();
            String cred = client.retrieveVaultCredential(resourceType, resourceName, resourceUserName);
            return Secret.fromString(cred);
        } catch (CentrifyVaultException e) {
            e.printStackTrace();
        }

        return Secret.fromString("");
    }

    @NonNull
    public String getResourceType() {
        return this.resourceType;
    }

    @DataBoundSetter
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @NonNull
    public String getResourceName() {
        return this.resourceName;
    }

    @DataBoundSetter
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @NonNull
    public String getResourceUserName() {
        return this.resourceUserName;
    }

    @DataBoundSetter
    public void setResourceUserName(String resourceUserName) {
        this.resourceUserName = resourceUserName;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.CentrifyVaultUsernamePasswordCredentialsImp_DisplayName();
        }

        public FormValidation doVerifyCredential(@QueryParameter("resourceType") String resourceType,
                @QueryParameter("resourceName") String resourceName,
                @QueryParameter("resourceUserName") String resourceUserName) {

            CentrifyVaultGlobalConfiguration globalConfig = CentrifyVaultGlobalConfiguration.get();
            CentrifyVaultConfiguration config = globalConfig.getCentrifyVaultConfiguration();

            if (resourceType == null || resourceType.isEmpty()) {
                return FormValidation
                        .error(Messages.CentrifyVaultUsernamePasswordCredentialsImp_ResourceTypeRequired());
            }
            if (resourceName == null || resourceName.isEmpty()) {
                return FormValidation
                        .error(Messages.CentrifyVaultUsernamePasswordCredentialsImp_ResourceNameRequired());
            }
            if (resourceUserName == null || resourceUserName.isEmpty()) {
                return FormValidation.error(Messages.CentrifyVaultUsernamePasswordCredentialsImp_UserNameRequired());
            }

            try {
                VaultClient client = config.getAuthenticatedVaultClient();
                String objectid = client.getCredentialID(resourceType, resourceName, resourceUserName);
                if (objectid != null) {
                    return FormValidation.ok(Messages.CentrifyVaultUsernamePasswordCredentialsImp_CredentialExists());
                } else {
                    return FormValidation
                            .error(Messages.CentrifyVaultUsernamePasswordCredentialsImp_CredentialNotExists());
                }
            } catch (CentrifyVaultException e) {
                return FormValidation.error(e.getMessage());
            }
        }

        @Override
        public String getIconClassName() {
            return "icon-centrify-credential";
        }

        static {
            IconSet.icons.addIcon(new Icon("icon-centrify-credential icon-sm",
                    "centrify-vault/images/16x16/centrify-credential-16x16.png", Icon.ICON_SMALL_STYLE,
                    IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon("icon-centrify-credential icon-md",
                    "centrify-vault/images/24x24/centrify-credential-24x24.png", Icon.ICON_MEDIUM_STYLE,
                    IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon("icon-centrify-credential icon-lg",
                    "centrify-vault/images/32x32/centrify-credential-32x32.png", Icon.ICON_LARGE_STYLE,
                    IconType.PLUGIN));
            IconSet.icons.addIcon(new Icon("icon-centrify-credential icon-xlg",
                    "centrify-vault/images/48x48/centrify-credential-48x48.png", Icon.ICON_XLARGE_STYLE,
                    IconType.PLUGIN));
        }
    }
}
