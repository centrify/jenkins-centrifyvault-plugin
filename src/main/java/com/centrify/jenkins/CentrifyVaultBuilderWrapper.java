package com.centrify.jenkins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.EnvVars;
import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import com.centrify.jenkins.configuration.CentrifyVaultConfiguration;
import com.centrify.jenkins.configuration.CentrifyVaultGlobalConfiguration;
import com.centrify.vault.VaultClient;
import com.centrify.vault.exceptions.CentrifyVaultException;

import java.io.PrintStream;

import javax.annotation.CheckForNull;
import java.util.List;

public class CentrifyVaultBuilderWrapper extends SimpleBuildWrapper {
    private CentrifyVaultConfiguration centrifyVaultConfiguration;
    private List<CentrifyVaultSecret> vaultSecrets;
    protected PrintStream logger;

    @DataBoundConstructor
    public CentrifyVaultBuilderWrapper(@CheckForNull List<CentrifyVaultSecret> vaultSecrets) {
        this.vaultSecrets = vaultSecrets;
    }

    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener,
            EnvVars initialEnvironment) {
        logger = listener.getLogger();

        if (null != vaultSecrets && !vaultSecrets.isEmpty()) {
            injectEnvFromVault(context, build, initialEnvironment);
        }
    }

    protected void injectEnvFromVault(Context context, Run<?, ?> build, EnvVars envVars) {
        CentrifyVaultGlobalConfiguration globalConfig = CentrifyVaultGlobalConfiguration.get();
        CentrifyVaultConfiguration config = globalConfig.getCentrifyVaultConfiguration();

        VaultClient client = new VaultClient();
        try {
            client = config.getAuthenticatedVaultClient();
        } catch (CentrifyVaultException e1) {
            e1.printStackTrace();
        }

        for (CentrifyVaultSecret vaultSecret : vaultSecrets) {
            String cred = null;
            try {
                cred = client.retrieveVaultCredential(vaultSecret.getResourceType(), vaultSecret.getResourceName(),
                        vaultSecret.getUserName());
            } catch (CentrifyVaultException e) {
                e.printStackTrace();
            }
            context.env(vaultSecret.getEnvVar(), cred);
        }
    }

    public CentrifyVaultConfiguration getCentrifyVaultConfiguration() {
        return this.centrifyVaultConfiguration;
    }

    public List<CentrifyVaultSecret> getVaultSecrets() {
        return this.vaultSecrets;
    }

    @DataBoundSetter
    public void setCentrifyVaultConfiguration(CentrifyVaultConfiguration centrifyVaultConfiguration) {
        this.centrifyVaultConfiguration = centrifyVaultConfiguration;
    }

    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(CentrifyVaultBuilderWrapper.class);
            load();
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Retrieve credential from Centrify Vault";
        }
    }

}
