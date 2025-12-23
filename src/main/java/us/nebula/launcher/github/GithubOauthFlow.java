/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher.github;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import us.nebula.launcher.util.Util;

import javax.swing.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class GithubOauthFlow
{
    private static final String GITHUB_OAUTH_CLIENT_ID = "Ov23li8Ort2oVwR4G4HB";

    private static final String GITHUB_REQUEST_DEVICE_CODE = String.format(
            "https://github.com/login/device/code?client_id=%s&scope=repo", GITHUB_OAUTH_CLIENT_ID);
    private static final String GITHUB_REQUEST_ACCESS_TOKEN =
            "https://github.com/login/oauth/access_token?client_id=" +
                    GITHUB_OAUTH_CLIENT_ID +
                    "&device_code=%s" +
                    "&grant_type=urn:ietf:params:oauth:grant-type:device_code";

    private String deviceCode, userCode;

    public String startVerification()
    {
        fetchCode();
        final int result = JOptionPane.showConfirmDialog(null,
                "Open https://github.com/login/device in your browser and enter the code\n"
                        + userCode
                        + "\nand then click OK when finished",
                "Github Authorization",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (result != 0)
        {
            throw new RuntimeException("bruih");
        }
        return fetchAccessToken();
    }

    private String fetchAccessToken()
    {
        try
        {
            final Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Accept", "application/json");
            final String content = Util.makeConnection(
                    "POST",
                    String.format(GITHUB_REQUEST_ACCESS_TOKEN, deviceCode),
                    headers,
                    null);
            final JsonObject object = new JsonParser().parse(content).getAsJsonObject();
            return object.get("access_token").getAsString();
        } catch (final IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    private void fetchCode()
    {
        try
        {
            final Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Accept", "application/json");
            final String content = Util.makeConnection(
                    "POST", GITHUB_REQUEST_DEVICE_CODE, headers, null);
            final JsonObject object = new JsonParser().parse(content).getAsJsonObject();
            deviceCode = object.get("device_code").getAsString();
            userCode = object.get("user_code").getAsString();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
