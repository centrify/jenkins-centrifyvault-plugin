package com.centrify.jenkins.credentials;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import com.centrify.jenkins.Messages;

import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.credentialsbinding.BindingDescriptor;
import org.jenkinsci.plugins.credentialsbinding.MultiBinding;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.Secret;

public class CentrifyVaultUsernameSSHKeyCredentialsBinding
        extends MultiBinding<CentrifyVaultUsernameSSHKeyCredentials> {
    private String usernameVariable;
    private String passphraseVariable;
    private String privatekeyVariable;

    @DataBoundConstructor
    public CentrifyVaultUsernameSSHKeyCredentialsBinding(String credentialsId) {
        super(credentialsId);
    }

    @Override
    protected Class<CentrifyVaultUsernameSSHKeyCredentials> type() {
        return CentrifyVaultUsernameSSHKeyCredentials.class;
    }

    @Override
    public MultiEnvironment bind(@Nonnull Run<?, ?> build, FilePath workspace, Launcher launcher,
            @Nonnull TaskListener listener) throws IOException, InterruptedException {
        CentrifyVaultUsernameSSHKeyCredentials credentials = this.getCredentials(build);

        Map<String, String> map = new HashMap<String, String>();
        if (credentials != null) {
            if (credentials.getUsername() != null) {
                map.put(this.usernameVariable, credentials.getUsername());
            }

            if (passphraseVariable != null) {
                if (credentials.getPassphrase() != null) {
                    Secret cred = credentials.getPassphrase();
                    if (cred != null) {
                        map.put(this.passphraseVariable, cred.getPlainText());
                    }
                } else {
                    map.put(this.passphraseVariable, "");
                }
            }

            if (credentials.getPrivateKey() != null) {
                map.put(this.privatekeyVariable, credentials.getPrivateKey());
            }
        }

        return new MultiEnvironment(map);
    }

    @Override
    public Set<String> variables() {
        Set<String> variables = new HashSet<String>();
        variables.add(this.usernameVariable);
        variables.add(this.passphraseVariable);
        variables.add(this.privatekeyVariable);

        return variables;
    }

    public String getUsernameVariable() {
        return this.usernameVariable;
    }

    public String getPassphraseVariable() {
        return this.passphraseVariable;
    }

    public String getPrivateKeyVariable() {
        return this.privatekeyVariable;
    }

    @DataBoundSetter
    public void setUsernameVariable(String usernameVariable) {
        this.usernameVariable = usernameVariable;
    }

    @DataBoundSetter
    public void setPassphraseVariable(String passphraseVariable) {
        this.passphraseVariable = passphraseVariable;
    }

    @DataBoundSetter
    public void setPrivatekeyVariable(String privatekeyVariable) {
        this.privatekeyVariable = privatekeyVariable;
    }

    @Symbol("centrifyVaultUsernameSSHKey")
    @Extension
    public static class DescriptorImpl extends BindingDescriptor<CentrifyVaultUsernameSSHKeyCredentials> {

        @Override
        public String getDisplayName() {
            return Messages.CentrifyVaultUsernameSSHKeyCredentialsImp_DisplayName();
        }

        @Override
        protected Class<CentrifyVaultUsernameSSHKeyCredentials> type() {
            return CentrifyVaultUsernameSSHKeyCredentials.class;
        }
    }
}
