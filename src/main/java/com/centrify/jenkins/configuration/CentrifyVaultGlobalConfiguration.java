package com.centrify.jenkins.configuration;

import hudson.Extension;
import javax.annotation.Nonnull;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundSetter;

@Extension
public class CentrifyVaultGlobalConfiguration extends GlobalConfiguration {
    private CentrifyVaultConfiguration centrifyVaultConfiguration;

    public CentrifyVaultGlobalConfiguration() {
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

    @Nonnull
    public static CentrifyVaultGlobalConfiguration get() {
        CentrifyVaultGlobalConfiguration instance = GlobalConfiguration.all()
                .get(CentrifyVaultGlobalConfiguration.class);
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    public CentrifyVaultConfiguration getCentrifyVaultConfiguration() {
        return this.centrifyVaultConfiguration;
    }

    @DataBoundSetter
    public void setCentrifyVaultConfiguration(CentrifyVaultConfiguration configuration) {
        this.centrifyVaultConfiguration = configuration;
    }

}
