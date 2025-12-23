/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher.github;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import us.nebula.launcher.util.NebulaMetadata;
import us.nebula.launcher.util.Util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;

public final class GithubAPIUtil
{
    private static final String CHECKSUM_BASE_URL = "https://raw.githubusercontent.com/xgraza/nebula-1.7.2/refs/heads/%s/dependencies/buildLibraries/%s.sha256";
    private static final String JAR_BASE_URL = "https://github.com/xgraza/nebula-1.7.2/raw/refs/heads/%s/dependencies/buildLibraries/%s";

    private static final String LIST_ACTION_RUNS_URL = "https://api.github.com/repos/xgraza/nebula-1.7.2/actions/runs";
    private static final String LIST_REPO_ARTIFACTS_URL = "https://api.github.com/repos/xgraza/nebula-1.7.2/actions/artifacts";
    //private static final String DOWNLOAD_ARTIFACT_URL = "https://github.com/xgraza/nebula-1.7.2/actions/runs/%s/artifacts/%s";
    private static final String DOWNLOAD_ARTIFACT_URL = "https://api.github.com/repos/xgraza/nebula-1.7.2/actions/artifacts/%s/zip";

    public static String getLatestReleaseURL()
    {
        try
        {
            String content = Util.makeConnection(
                    "GET", LIST_ACTION_RUNS_URL, null, null);
            JsonObject selectedWorkflowRunObject = null;
            {
                final JsonObject object = new JsonParser().parse(content).getAsJsonObject();
                final JsonArray workflowRunsArray = object.get("workflow_runs").getAsJsonArray();

                for (final JsonElement element : workflowRunsArray)
                {
                    if (!element.isJsonObject())
                    {
                        continue;
                    }
                    final JsonObject workflowRunObject = element.getAsJsonObject();
                    if (!workflowRunObject.get("conclusion").getAsString().equals("success"))
                    {
                        continue;
                    }
                    selectedWorkflowRunObject = workflowRunObject;
                    break;
                }
                if (selectedWorkflowRunObject == null)
                {
                    return null;
                }
            }

            final BigInteger workflowId = selectedWorkflowRunObject.get("id").getAsBigInteger();

            // find artifact for this workflow run
            content = Util.makeConnection("GET", LIST_REPO_ARTIFACTS_URL, null, null);
            JsonObject selectedArtifactObject = null;
            {
                final JsonObject object = new JsonParser().parse(content).getAsJsonObject();
                final JsonArray artifactsArray = object.get("artifacts").getAsJsonArray();

                for (final JsonElement element : artifactsArray)
                {
                    if (!element.isJsonObject())
                    {
                        continue;
                    }
                    final JsonObject artifactObject = element.getAsJsonObject();
                    final JsonObject workflowRunObject = artifactObject.get("workflow_run").getAsJsonObject();
                    if (Objects.equals(workflowRunObject.get("id").getAsBigInteger(), workflowId))
                    {
                        selectedArtifactObject = artifactObject;
                        break;
                    }
                }
                if (selectedArtifactObject == null)
                {
                    return null;
                }
            }

            final BigInteger artifactId = selectedArtifactObject.get("id").getAsBigInteger();

//            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//            if (headers != null)
//            {
//                headers.forEach(connection::setRequestProperty);
//            }
//            connection.connect();
//            System.out.println(connection.getURL().toString());

            return String.format(DOWNLOAD_ARTIFACT_URL, artifactId.toString());
        } catch (final IOException exception)
        {
            return null;
        }
    }

    public static String getGithubChecksum(final String fileName)
    {
        try
        {
            return Util.makeConnection("GET",
                    String.format(CHECKSUM_BASE_URL,
                            NebulaMetadata.BRANCH, fileName),
                    null,
                    null);
        } catch (final IOException exception)
        {
            return null;
        }
    }

    private GithubAPIUtil()
    {
        // no-op
    }
}
