package com.centrify.jenkins.credentials;

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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.Secret;

import com.centrify.jenkins.Messages;

public class CentrifyVaultStringCredentialsImp extends BaseStandardCredentials
        implements CentrifyVaultStringCredentials {
    private static final long serialVersionUID = 1L;
    private String parentPath;
    private String secretName;

    @DataBoundConstructor
    public CentrifyVaultStringCredentialsImp(@CheckForNull CredentialsScope scope, @CheckForNull String id,
            @CheckForNull String description) {
        super(scope, id, description);
    }

    @Override
    public Secret getSecret() {
        CentrifyVaultGlobalConfiguration globalConfig = CentrifyVaultGlobalConfiguration.get();
        CentrifyVaultConfiguration config = globalConfig.getCentrifyVaultConfiguration();

        try {
            VaultClient client = config.getAuthenticatedVaultClient();
            String cred = client.retrieveVaultCredential("secret", parentPath, secretName);
            return Secret.fromString(cred);
        } catch (CentrifyVaultException e) {
            e.printStackTrace();
        }

        return Secret.fromString("");
    }

    @NonNull
    public String getParentPath() {
        return this.parentPath;
    }

    @DataBoundSetter
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    @NonNull
    @Override
    public String getSecretName() {
        return this.secretName;
    }

    @DataBoundSetter
    public void setSecretName(String secretName) {
        this.secretName = secretName;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.CentrifyVaultStringCredentialsImp_DisplayName();
        }

        public FormValidation doVerifySecret(@QueryParameter("parentPath") String parentPath,
                @QueryParameter("secretName") String secretName) {

            CentrifyVaultGlobalConfiguration globalConfig = CentrifyVaultGlobalConfiguration.get();
            CentrifyVaultConfiguration config = globalConfig.getCentrifyVaultConfiguration();

            if (secretName == null || secretName.isEmpty()) {
                return FormValidation.error(Messages.CentrifyVaultStringCredentialsImp_SecretNameRequired());
            }

            final String resourceType = "secret";
            try {
                VaultClient client = config.getAuthenticatedVaultClient();
                String objectid = client.getCredentialID(resourceType, parentPath, secretName);
                if (objectid != null) {
                    return FormValidation.ok(Messages.CentrifyVaultStringCredentialsImp_SecretExists());
                } else {
                    return FormValidation.error(Messages.CentrifyVaultStringCredentialsImp_SecretNotExists());
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
