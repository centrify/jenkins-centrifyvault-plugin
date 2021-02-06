package com.centrify.jenkins.credentials;

import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.NameWith;

import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;

@NameWith(value = CentrifyVaultStringCredentials.NameProvider.class, priority = 32)
public interface CentrifyVaultStringCredentials extends StringCredentials {

    String getSecretName();

    class NameProvider extends CredentialsNameProvider<CentrifyVaultStringCredentials> {

        @NonNull
        @Override
        public String getName(CentrifyVaultStringCredentials c) {
            String description = Util.fixEmpty(c.getDescription());
            return c.getSecretName() + (description == null ? "" : " (" + description + ")");
        }
    }
}
