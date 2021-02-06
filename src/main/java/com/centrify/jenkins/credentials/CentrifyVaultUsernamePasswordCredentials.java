package com.centrify.jenkins.credentials;

import java.io.IOException;

import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.NameWith;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.Util;
import hudson.util.Secret;

@NameWith(value = CentrifyVaultUsernamePasswordCredentials.NameProvider.class, priority = 32)
public interface CentrifyVaultUsernamePasswordCredentials extends StandardUsernamePasswordCredentials {

    Secret getSecret() throws IOException, InterruptedException;
    
    class NameProvider extends CredentialsNameProvider<CentrifyVaultUsernamePasswordCredentials> {

        @NonNull
        @Override
        public String getName(@NonNull CentrifyVaultUsernamePasswordCredentials c) {
            String description = Util.fixEmptyAndTrim(c.getDescription());
            return c.getUsername() + "/******" + (description != null ? " (" + description + ")" : "");
        }
    }
}
