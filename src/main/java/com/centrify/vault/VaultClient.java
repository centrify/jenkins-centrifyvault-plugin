package com.centrify.vault;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.centrify.vault.platform.*;

import com.centrify.vault.exceptions.CentrifyVaultException;

public class VaultClient {
    private static final Logger LOGGER = Logger.getLogger(VaultClient.class.getName());

    private String tenantURL;
    private String appID;
    private String scope;
    private String clientID;
    private String clientSecret;
    private String accessToken;
    private boolean debug;

    public VaultClient() {
    }

    public VaultClient(String url, String appid, String scope, String clientid, String secret) {
        this(url, appid, scope, clientid, secret, false);
    }

    public VaultClient(String url, String appid, String scope, String clientid, String secret, boolean debug) {
        // Remove the last "/" charadter from url if any
        if (url != null && url.length() > 0 && url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, (url.length() - 1));
        }
        this.tenantURL = url;
        this.appID = appid;
        this.scope = scope;
        this.clientID = clientid;
        this.clientSecret = secret;
        this.debug = debug;
        if (this.debug) {
            VaultUtil.setDebugLevel(Level.INFO);
        } else {
            VaultUtil.setDebugLevel(Level.OFF);
        }
    }

    public void setTenantURL(String url) {
        this.tenantURL = url;
    }

    public void setAppID(String appid) {
        this.appID = appid;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setClientID(String clientid) {
        this.clientID = clientid;
    }

    public void setClientSecret(String secret) {
        this.clientSecret = secret;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void connectToVault() throws CentrifyVaultException {
        String auth = clientID + ":" + clientSecret;
        String authentication = Base64.getEncoder().encodeToString(auth.getBytes(Charset.forName("UTF-8")));

        // Construct HTTP header
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "application/json");
        headers.put("X-CENTRIFY-NATIVE-CLIENT", "true");
        headers.put("X-CFY-SRC", "Java SDK");
        headers.put("Authorization", "Basic " + authentication);

        // Construct request parameters
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("scope", scope);
        byte[] postDataBytes = VaultUtil.getPostData(params);

        String url = tenantURL + "/oauth2/token/";
        String response = VaultUtil.postAndRespond(url + appID, headers, postDataBytes);

        // Find access token
        final Pattern pat = Pattern.compile(".*\"access_token\"\\s*:\\s*\"([^\"]+)\".*");
        Matcher matcher = pat.matcher(response);
        if (matcher.matches() && matcher.groupCount() > 0) {
            accessToken = matcher.group(1);
            LOGGER.info("Successfully authenticated to " + url);
        } else {
            throw new CentrifyVaultException("Failed to get authenticated connection with response (" + response + ")");
        }
    }

    public String genericRestCall(String method, Map<String, Object> postdata, Map<String, Object> args) {
        String response = null;

        // Construct HTTP header
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-CENTRIFY-NATIVE-CLIENT", "true");
        headers.put("X-CFY-SRC", "Java SDK");
        headers.put("Authorization", "Bearer " + accessToken);

        if (args != null) {
            postdata.put("args", args);
        }

        try {
            String json = new ObjectMapper().writeValueAsString(postdata);
            LOGGER.info("JSON post data: " + json);
            byte[] postDataBytes = json.getBytes("utf-8");
            response = VaultUtil.postAndRespond(tenantURL + method, headers, postDataBytes);
            //if (response == null)
            //    throw new NullPointerException();
        } catch (CentrifyVaultException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public <T extends VaultObject> T queryVaultObject(String query, Class<T> valueType) {
        T object = null;

        // Construct HTTP header
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-CENTRIFY-NATIVE-CLIENT", "true");
        headers.put("X-CFY-SRC", "Java SDK");
        headers.put("Authorization", "Bearer " + accessToken);

        // Construct request data
        Map<String, Object> args = new HashMap<>();
        args.put("Caching", -1);

        Map<String, Object> params = new HashMap<>();
        params.put("args", args);
        params.put("Script", query);

        try {
            String json = new ObjectMapper().writeValueAsString(params);
            LOGGER.info("JSON query string: " + json);
            byte[] postDataBytes = json.getBytes("utf-8");
            String url = tenantURL + "/RedRock/query";
            String response = VaultUtil.postAndRespond(url, headers, postDataBytes);

            ObjectMapper mapper = new ObjectMapper();
            // mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
            // mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

            // RestResponseGenericMap obj = mapper.readValue(response,
            // RestResponseGenericMap.class);
            // String prettyObj =
            // mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            // LOGGER.info(prettyObj);

            JsonNode root = mapper.readTree(response);
            JsonNode resultBlock = root.path("Result");
            if (!resultBlock.isMissingNode()) {
                JsonNode resultList = resultBlock.path("Results");
                if (resultList.isArray()) {
                    if (resultList.size() == 0) {
                        throw new Exception("Query returns 0 object");
                    }
                    if (resultList.size() > 1) {
                        throw new Exception("Query returns too many objects");
                    }
                    JsonNode objectBlock = resultList.get(0).path("Row");

                    object = mapper.treeToValue(objectBlock, valueType);
                    String prettyObj = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
                    LOGGER.info(prettyObj);

                } else {
                    throw new Exception("Incorrect response received");
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    public <T extends VaultObject> String queryVaultObjectID(String query, Class<T> valueType) {
        String ID = null;
        T object = queryVaultObject(query, valueType);
        if (object != null) {
            ID = object.getID();
        }

        return ID;
    }

    public String getCredentialID(String resourceType, String resourceName, String credName)
            throws CentrifyVaultException {
        String objectid = null;
        String resourceID = null;

        VaultAccount account = new VaultAccount();
        account.setUserName(credName);

        VaultSecret secret = new VaultSecret();
        secret.setSecretName(credName);

        switch (resourceType.toLowerCase()) {
            case "system":
                VaultSystem system = new VaultSystem();
                system.setName(resourceName);
                resourceID = queryVaultObjectID(system.getQueryStatement(), VaultSystem.class);
                account.setHostID(resourceID);
                break;
            case "database":
                VaultDatabase database = new VaultDatabase();
                database.setName(resourceName);
                resourceID = queryVaultObjectID(database.getQueryStatement(), VaultDatabase.class);
                account.setDatabaseID(resourceID);
                break;
            case "domain":
                VaultDomain domain = new VaultDomain();
                domain.setName(resourceName);
                resourceID = queryVaultObjectID(domain.getQueryStatement(), VaultDomain.class);
                account.setDomainID(resourceID);
                break;
            case "sshkey":
                VaultSSHKey sshkey = new VaultSSHKey();
                sshkey.setName(credName);
                resourceID = queryVaultObjectID(sshkey.getQueryStatement(), VaultSSHKey.class);
                break;
            case "secret":
                // In certain UI form if parent path field is empty, its value is null which results in inccorect query result
                // So, set the parent path value to empty string to indicate the secret is in root level
                if (resourceName == null) {
                    resourceName = "";
                }
                secret.setParentPath(resourceName);
                resourceID = queryVaultObjectID(secret.getQueryStatement(), VaultSecret.class);
                break;
            default:
                throw new CentrifyVaultException("Invalid resource type " + resourceType);
        }

        if (resourceID == null) {
            throw new CentrifyVaultException("Resource " + resourceName + " with type " + resourceType + " does not exist");
        }

        if (resourceType.equalsIgnoreCase("secret")) {
            objectid = queryVaultObjectID(secret.getQueryStatement(), VaultSecret.class);
        } else if (resourceType.equalsIgnoreCase("sshkey")) {
            objectid = resourceID;
        } else {
            objectid = queryVaultObjectID(account.getQueryStatement(), VaultAccount.class);
        }

        LOGGER.info("Found object ID: " + objectid + " for credential name:" + credName + " in: " + resourceName + " with type: " + resourceType);

        return objectid;
    }

    public String retrieveVaultCredential(String resourceType, String resourceName, String credName)
            throws CentrifyVaultException {

        String credential = null;
        String resourceID = getCredentialID(resourceType, resourceName, credName);
        if (resourceID == null) {
            throw new CentrifyVaultException("Unable to find " + credName + " for " + resourceName);
        }

        switch (resourceType.toLowerCase()) {
            case "secret":
                VaultSecret secret = new VaultSecret();
                secret.setSecretName(credName);
                secret.setID(resourceID);
                credential = checkoutSecret(secret);
                break;
            default:
                VaultAccount account = new VaultAccount();
                account.setUserName(credName);
                account.setID(resourceID);
                credential = checkOutPassword(account, true);
        }

        LOGGER.info("Retrieved credential for " + credName + " in: " + resourceName + " with type: " + resourceType);
        return credential;
    }

    public String checkOutPassword(VaultAccount acct) {
        return checkOutPassword(acct, false);
    }

    public String checkOutPassword(VaultAccount acct, boolean checkin) {
        String pw = null;
        String coid = null;

        if (acct.getID() != null) {
            Map<String, Object> postdata = new HashMap<>();
            postdata.put("ID", acct.getID());
            postdata.put("Description", "Checkout by Java SDK");
            String response = genericRestCall(acct.apiCheckoutPassword, postdata, null);
            
            ObjectMapper mapper = new ObjectMapper();
            try {
                RestResponseGenericMap result = mapper.readValue(response, RestResponseGenericMap.class);
                pw = (String) result.getResult().get("Password");
                coid = (String) result.getResult().get("COID");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        if (checkin && coid != null) {
            checkInPassword(acct, coid);
        }

        return pw;
    }

    public void checkInPassword(VaultAccount acct, String coid) {
        if (coid != null) {
            Map<String, Object> postdata = new HashMap<>();
            postdata.put("ID", coid);
            String response = genericRestCall(acct.apiCheckinPassword, postdata, null);
            LOGGER.info(response);
        }
    }

    // Retrieve SSHKey by its name
    public String retrieveVaultSSHKeyByName(String name, String keyType, String passphrase) {
        VaultSSHKey sshkey = new VaultSSHKey();
        sshkey.setName(name);
        String sshkeyID = queryVaultObjectID(sshkey.getQueryStatement(), VaultSSHKey.class);
        sshkey.setID(sshkeyID);
        sshkey.setKeyPairType(keyType);
        sshkey.setKeyFormat("PEM");
        sshkey.setPassphrase(passphrase);

        String key = retrieveSSHKey(sshkey);
        LOGGER.info("Retrieved SSHKey with name " + sshkey.getName());

        return key;
    }

    // Retrieve SSHKey that is associted with a vaulted account 
    public String retrieveVaultSSHKeyByAccount(String systemName, String userName, String keyType, String passphrase) {
        // Find out system ID first
        VaultSystem system = new VaultSystem();
        system.setName(systemName);
        String resourceID = queryVaultObjectID(system.getQueryStatement(), VaultSystem.class);

        // Build account object
        VaultAccount account = new VaultAccount();
        account.setUserName(userName);
        account.setHostID(resourceID);
        VaultAccount actualAcccount = queryVaultObject(account.getQueryStatement(), VaultAccount.class);
        // If the account uses SSHKey, there should be CredentialID in it
        String sshkeyid = actualAcccount.getCredentialID();

        VaultSSHKey sshkey = new VaultSSHKey();
        sshkey.setID(sshkeyid);
        sshkey.setKeyPairType(keyType);
        sshkey.setKeyFormat("PEM");
        sshkey.setPassphrase(passphrase);

        String key = retrieveSSHKey(sshkey);
        LOGGER.info("Retrieved SSHKey for account " + userName + " in system " + systemName);

        return key;
    }

    public String retrieveSSHKey(VaultSSHKey sshkey) {
        String privatekey = null;

        if (sshkey.getID() != null) {
            Map<String, Object> postdata = new HashMap<>();
            postdata.put("KeyPairType", sshkey.getKeyPairType());
            postdata.put("KeyFormat", sshkey.getKeyFormat());
            String passphrase = sshkey.getPassphrase();
            if (passphrase != null && !passphrase.isEmpty()) {
                postdata.put("Passphrase", passphrase);
            }
            postdata.put("ID", sshkey.getID());
            postdata.put("Description", "Checkout by Java SDK");
            String response = genericRestCall(sshkey.apiRetrieve, postdata, null);
            
            ObjectMapper mapper = new ObjectMapper();
            try {
                RestResponseString result = mapper.readValue(response, RestResponseString.class);
                privatekey = result.getResult();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return privatekey;
    }

    public String checkoutSecret(VaultSecret secret) {
        String secretText = null;

        if (secret.getID() != null) {
            Map<String, Object> postdata = new HashMap<>();
            postdata.put("ID", secret.getID());
            postdata.put("Description", "Checkout by Java SDK");
            String response = genericRestCall(secret.apiRetrieveSecret, postdata, null);
            
            ObjectMapper mapper = new ObjectMapper();
            try {
                RestResponseGenericMap result = mapper.readValue(response, RestResponseGenericMap.class);
                secretText = (String) result.getResult().get("SecretText");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return secretText;
    }
}
