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

public class CentrifyVaultStringCredentialsBinding extends MultiBinding<CentrifyVaultStringCredentials> {
    private String secretVariable;

    @DataBoundConstructor
    public CentrifyVaultStringCredentialsBinding(String credentialsId) {
        super(credentialsId);
    }

    @Override
    protected Class<CentrifyVaultStringCredentials> type() {
        return CentrifyVaultStringCredentials.class;
    }

    @Override
    public MultiEnvironment bind(@Nonnull Run<?, ?> build, FilePath workspace, Launcher launcher,
            @Nonnull TaskListener listener) throws IOException, InterruptedException {
        CentrifyVaultStringCredentials credentials = getCredentials(build);

        Map<String, String> map = new HashMap<String, String>();
        if (credentials.getSecret() != null)
            map.put(this.secretVariable, credentials.getSecret().getPlainText());

        return new MultiEnvironment(map);
    }

    @Override
    public Set<String> variables() {
        Set<String> variables = new HashSet<String>();
        variables.add(this.secretVariable);

        return variables;
    }

    public String getSecretVariable() {
		return this.secretVariable;
    }
    
    @DataBoundSetter
	public void setSecretVariable(String secretVariable) {
		this.secretVariable = secretVariable;
    }
    
    @Symbol("centrifyVaultSecretText")
	@Extension
	public static class DescriptorImpl extends BindingDescriptor<CentrifyVaultStringCredentials> {

		@Override
		public String getDisplayName() {
            return Messages.CentrifyVaultStringCredentialsImp_DisplayName();
		}

		@Override
		protected Class<CentrifyVaultStringCredentials> type() {
			return CentrifyVaultStringCredentials.class;
		}
	}
}
