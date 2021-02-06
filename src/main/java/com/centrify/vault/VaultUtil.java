package com.centrify.vault;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import com.centrify.vault.exceptions.CentrifyVaultException;

public class VaultUtil {
    private static final Logger LOGGER = Logger.getLogger(VaultUtil.class.getName());

    public static void setDebugLevel(Level newLvl) {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        rootLogger.setLevel(newLvl);
        for (Handler h : handlers) {
            h.setLevel(newLvl);
        }
    }

    public static byte[] getPostData(Map<String, String> params) {
        byte[] postDataBytes = null;
        StringBuilder postData = new StringBuilder();

        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            try {
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            postDataBytes = postData.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return postDataBytes;
    }

    public static String postAndRespond(String posturl, Map<String, String> headers, byte[] data)
            throws CentrifyVaultException {
        String response = null;
        HttpsURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // String actionURL = tenantURL + method;
            URL url = new URL(posturl);
            LOGGER.info("Post URL: " + posturl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.write(data);
            writer.flush();
            writer.close();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line = null;
            StringWriter out = new StringWriter(
                    connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            response = out.toString();
            // LOGGER.info(response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new CentrifyVaultException(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage());
                    throw new CentrifyVaultException(e.getMessage());
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }
}
