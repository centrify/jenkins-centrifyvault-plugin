package com.centrify.jenkins.credentials;

import com.cloudbees.jenkins.plugins.sshcredentials.SSHUserPrivateKey;
import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.NameWith;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;

@NameWith(value = CentrifyVaultUsernameSSHKeyCredentials.NameProvider.class, priority = 32)
public interface CentrifyVaultUsernameSSHKeyCredentials extends SSHUserPrivateKey {
    String getPrivateKey();

    class NameProvider extends CredentialsNameProvider<CentrifyVaultUsernameSSHKeyCredentials> {

        @NonNull
        @Override
        public String getName(@NonNull CentrifyVaultUsernameSSHKeyCredentials c) {
            String description = Util.fixEmptyAndTrim(c.getDescription());
            return c.getUsername() + "/******" + (description != null ? " (" + description + ")" : "");
        }
    }
}
