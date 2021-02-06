package com.centrify.jenkins.credentials;

import java.util.Collections;
import java.util.List;

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
import com.centrify.jenkins.Messages;

public class CentrifyVaultUsernameSSHKeyCredentialsImp extends BaseStandardCredentials
        implements CentrifyVaultUsernameSSHKeyCredentials {
    private static final long serialVersionUID = 1L;
    private Secret passphrase;
    private String resourceName;
    private String resourceUserName;
    private String sshkeyName;
    private boolean associatedWithAccount;

    @DataBoundConstructor
    public CentrifyVaultUsernameSSHKeyCredentialsImp(CredentialsScope scope, String id, String description) {
        super(scope, id, description);
    }

    public boolean getAssociatedWithAccount() {
        return associatedWithAccount;
    }

    @DataBoundSetter
    public void setAssociatedWithAccount(boolean associatedWithAccount) {
        this.associatedWithAccount = associatedWithAccount;
    }

    public boolean associatedWithAccount() {
        return associatedWithAccount;
    }

    @Override
    public Secret getPassphrase() {
        return passphrase;
    }

    @DataBoundSetter
    public void setPassphrase(final Secret passphrase) {
        this.passphrase = passphrase;
    }

    @Override
    public List<String> getPrivateKeys() {
        return Collections.singletonList(getPrivateKey());
    }

    @Override
    public String getUsername() {
        return this.resourceUserName;
    }

    @Override
    public String getPrivateKey() {
        CentrifyVaultGlobalConfiguration globalConfig = CentrifyVaultGlobalConfiguration.get();
        CentrifyVaultConfiguration config = globalConfig.getCentrifyVaultConfiguration();

        try {
            VaultClient client = config.getAuthenticatedVaultClient();
            if (associatedWithAccount) {
                return client.retrieveVaultSSHKeyByAccount(resourceName, resourceUserName, "PrivateKey",
                        passphrase.getPlainText());
            } else {
                return client.retrieveVaultSSHKeyByName(sshkeyName, "PrivateKey", passphrase.getPlainText());
            }
        } catch (CentrifyVaultException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getResourceName() {
        return this.resourceName;
    }

    @DataBoundSetter
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getSshkeyName() {
        return this.sshkeyName;
    }

    @DataBoundSetter
    public void setSshkeyName(String sshkeyName) {
        this.sshkeyName = sshkeyName;
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
            return Messages.CentrifyVaultUsernameSSHKeyCredentialsImp_DisplayName();
        }

        public FormValidation doVerifyCredential(@QueryParameter("associatedWithAccount") boolean associatedWithAccount,
                @QueryParameter("resourceName") String resourceName, @QueryParameter("sshkeyName") String sshkeyName,
                @QueryParameter("resourceUserName") String resourceUserName,
                @QueryParameter("passphrase") Secret passphrase) {

            CentrifyVaultGlobalConfiguration globalConfig = CentrifyVaultGlobalConfiguration.get();
            CentrifyVaultConfiguration config = globalConfig.getCentrifyVaultConfiguration();

            if (associatedWithAccount && (resourceName == null || resourceName.isEmpty())) {
                return FormValidation.error(Messages.CentrifyVaultUsernameSSHKeyCredentialsImp_ResourceNameRequired());
            }

            if (!associatedWithAccount && (sshkeyName == null || sshkeyName.isEmpty())) {
                return FormValidation.error(Messages.CentrifyVaultUsernameSSHKeyCredentialsImp_SSHKeyNameRequired());
            }

            if (resourceUserName == null || resourceUserName.isEmpty()) {
                return FormValidation.error(Messages.CentrifyVaultUsernameSSHKeyCredentialsImp_UserNameRequired());
            }

            try {
                VaultClient client = config.getAuthenticatedVaultClient();
                String objectid = null;
                if (associatedWithAccount) {
                    objectid = client.getCredentialID("system", resourceName, resourceUserName);
                } else {
                    objectid = client.getCredentialID("sshkey", null, sshkeyName);
                }

                if (objectid != null) {
                    return FormValidation.ok(Messages.CentrifyVaultUsernameSSHKeyCredentialsImp_SSHKeyExists());
                } else {
                    return FormValidation.error(Messages.CentrifyVaultUsernameSSHKeyCredentialsImp_SSHKeyNotExists());
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
