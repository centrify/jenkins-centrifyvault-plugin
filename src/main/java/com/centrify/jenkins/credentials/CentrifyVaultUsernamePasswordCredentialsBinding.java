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

public class CentrifyVaultUsernamePasswordCredentialsBinding
        extends MultiBinding<CentrifyVaultUsernamePasswordCredentials> {
    private String usernameVariable;
    private String passwordVariable;

    @DataBoundConstructor
    public CentrifyVaultUsernamePasswordCredentialsBinding(String credentialsId) {
        super(credentialsId);
    }

    @Override
    public MultiEnvironment bind(@Nonnull Run<?, ?> build, FilePath workspace, Launcher launcher,
            @Nonnull TaskListener listener) throws IOException, InterruptedException {
        CentrifyVaultUsernamePasswordCredentials credentials = this.getCredentials(build);

        Map<String, String> map = new HashMap<String, String>();
        if (credentials.getUsername() != null)
            map.put(this.usernameVariable, credentials.getUsername());
        if (credentials.getPassword() != null)
            map.put(this.passwordVariable, credentials.getPassword().getPlainText());

        return new MultiEnvironment(map);
    }

    @Override
    protected Class<CentrifyVaultUsernamePasswordCredentials> type() {
        return CentrifyVaultUsernamePasswordCredentials.class;
    }

    @Override
    public Set<String> variables() {
        Set<String> variables = new HashSet<String>();
        variables.add(this.usernameVariable);
        variables.add(this.passwordVariable);

        return variables;
    }

    public String getPasswordVariable() {
        return this.passwordVariable;
    }

    public String getUsernameVariable() {
        return this.usernameVariable;
    }

    @DataBoundSetter
    public void setPasswordVariable(String passwordVariable) {
        this.passwordVariable = passwordVariable;
    }

    @DataBoundSetter
    public void setUsernameVariable(String usernameVariable) {
        this.usernameVariable = usernameVariable;
    }

    @Symbol("centrifyVaultUsernamePassword")
    @Extension
    public static class DescriptorImpl extends BindingDescriptor<CentrifyVaultUsernamePasswordCredentials> {

        @Override
        public String getDisplayName() {
            return Messages.CentrifyVaultUsernamePasswordCredentialsImp_DisplayName();
        }

        @Override
        protected Class<CentrifyVaultUsernamePasswordCredentials> type() {
            return CentrifyVaultUsernamePasswordCredentials.class;
        }
    }
}
