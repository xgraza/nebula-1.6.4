package us.nebula.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author xgraza
 * @since 12/20/25
 */
class PublishBuildDataTask extends DefaultTask {
    private static var GITHUB_URL = "https://api.github.com/gists/9b7e94ddf127f108feac982e087a5bc6"

    @Input
    String directory
    @Input
    String gitHash
    @Input
    String buildID
    @Input
    String version

    @TaskAction
    void run() {
        println "Publishing data:\nGit-Hash: $gitHash\nBuildID: $buildID\nVersion: $version"
        var properties = readProperties(Paths.get(directory)
                .resolve("publishing.properties"))
        if (properties.isEmpty()) {
            println "ERROR!! Failed to read properties..."
            return
        }
        updateGist(properties.getProperty("github_token"))
    }

    void updateGist(String token) {
        // the best code you will see in your entire life...
        var connection = new URL(GITHUB_URL).openConnection() as HttpURLConnection
        connection.setRequestMethod("POST")
        connection.setConnectTimeout(5000)
        connection.setReadTimeout(5000)
        connection.setDoOutput(true)
        connection.setRequestProperty("Accept", "application/vnd.github+json")
        connection.setRequestProperty("Authorization", "Bearer $token")
        connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")

        var os = connection.getOutputStream()
        var bytes = "{\"description\":\"\",\"files\":{\"version.txt\":{\"content\":\"${gitHash},${buildID},${version}\"}}}"
                .getBytes()
        os.write(bytes, 0, bytes.length)

        connection.connect()

        switch (connection.responseCode) {
            case 200: {
                println "Success! Posted."
                break
            }
            case 404: {
                println "Gist not found..."
                break
            }
            case 422: {
                println "Validation failed/spammed endpoint"
                break
            }
            default: {
                println "Unhandled responseCode: $connection.responseCode"
                break
            }
        }
    }

    static Properties readProperties(Path path) {
        var properties = new Properties()
        properties.load(Files.newInputStream(path))
        return properties
    }
}
