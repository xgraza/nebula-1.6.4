package us.nebula.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * @author xgraza
 * @since 1.0.0
 */
class UploadBuildToServerTask extends DefaultTask {
    private static final var SERVER_HOST = "http://localhost"
    private static final var SERVER_PORT = 8080
    private static final File TEMP_ZIP = new File(Util.USER_DIR, "tmp.zip")

    @Input
    String buildDirectory

    @Input
    String librariesDirectory

    @TaskAction
    void run() {
        TEMP_ZIP.deleteOnExit()
        try {
            createZip()
            uploadToServer()
        } catch (exception) {
            exception.printStackTrace()
        }
    }

    void createZip() throws Exception {
        var files = getFilesForZip(buildDirectory, librariesDirectory)
        if (files == null || files.isEmpty()) {
            throw new RuntimeException("Failed to get files for zip")
        }

        println "Zipping contents to $TEMP_ZIP.absolutePath"

        try (var zos = new ZipOutputStream(Files.newOutputStream(TEMP_ZIP.toPath()))) {
            for (var file in files) {
                println "\tZipping $file.name (${(file.size() / 1024.0 / 1024.0).round(2)} MB)"
                zos.putNextEntry(new ZipEntry(file.name))
                try (var fis = Files.newInputStream(file.toPath())) {
                    int b
                    while ((b = fis.read()) != -1) {
                        zos.write(b)
                    }
                }
                zos.closeEntry()
            }
            zos.finish()

            println "Zipped ${files.size()} files to $TEMP_ZIP"
        }
    }

    static void uploadToServer() throws IOException {
        println "Enter password below:"
        var scanner = new Scanner(System.in)
        var password = scanner.nextLine()
        scanner.close()

        println "Attempting upload to ${SERVER_HOST}:${SERVER_PORT}"

        var connection = new URL(SERVER_HOST + ":" + SERVER_PORT + "/upload")
                .openConnection() as HttpURLConnection
        connection.setConnectTimeout(5000)
        connection.setReadTimeout(5000)
        connection.setDoOutput(true)
        connection.setRequestMethod("PUT")
        connection.setRequestProperty("Authorization", password)

        var os = connection.getOutputStream()
        try (var is = Files.newInputStream(TEMP_ZIP.toPath())) {
            int b
            while ((b = is.read()) != -1) {
                os.write(b)
            }
        }
        os.close()

        println "\tAttempting to connect..."
        connection.connect()

        if (connection.responseCode == 200) {
            println "\tUploaded file successfully!"
            connection.inputStream.close()
        } else {
            println "ERROR: Failed to upload file (${connection.responseCode})"
            connection.errorStream.readLines().forEach {
                println it
            }
            connection.errorStream.close()
        }

        connection.disconnect()
    }

    static List<File> getFilesForZip(String buildDirectory, String librariesDirectory) {
        var nebulaBuildFile = new File(buildDirectory, "nebula-rewrite-4.0.0.jar")
        if (!nebulaBuildFile.exists()) {
            println "ERROR: Nebula build file does not exist"
            return null
        }

        var files = new LinkedList<File>()
        var libraries = new File(librariesDirectory)
        for (var file in libraries.listFiles()) {
            if (!file.name.endsWithIgnoreCase(".jar")) {
                println "WARN: $file is not a jar file in the libraries path"
                continue
            }
            files.add(file)
        }

        files.add(nebulaBuildFile)
        return files
    }
}
