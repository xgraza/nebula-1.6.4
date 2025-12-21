package us.nebula.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.security.MessageDigest

/**
 * @author xgraza
 * @since 12/20/25
 */
class GenerateLibraryChecksumTask extends DefaultTask {
    private static final MessageDigest SHA256_DIGEST

    static {
        try {
            SHA256_DIGEST = MessageDigest.getInstance("SHA-256")
        } catch (exception) {
            println "Failed to get digest algorithm for SHA-256"
            exception.printStackTrace()
        }
        if (SHA256_DIGEST == null) {
            throw new RuntimeException("SHA-256 digest algorithm is null")
        }
    }

    @Input
    String directory

    @TaskAction
    void run() {
        var libPath = new File(directory)
        if (!libPath.exists() || !libPath.isDirectory()) {
            println "$directory is not a valid directory path"
            return
        }
        var libraries = listLibraries(libPath)
        if (libraries.isEmpty()) {
            println "Empty libraries folder. Aborting now..."
            return
        }
        println "Creating checksums for ${libraries.size()} libraries"
        libraries.forEach {
            try {
                var file = generateChecksumFile(it)
                if (file == null) {
                    println "Failed to generate checksum for $it"
                } else {
                    println "Checksum for $it.name at $file.name"
                }
            } catch (exception) {
                println "Failed to generate checksum for $it"
                exception.printStackTrace()
            }
        }
    }

    static File generateChecksumFile(File file) {
        var checksumFile = new File(file.absolutePath + ".sha256")
        if (checksumFile.exists() && !checksumFile.delete()) {
            println "WARN: Failed to delete previous checksum file $checksumFile"
        }

        if (!checksumFile.exists()) {
            checksumFile.createNewFile()
        }

        byte[] digestBytes
        try (var stream = new BufferedInputStream(new FileInputStream(file))) {
            digestBytes = SHA256_DIGEST.digest(stream.bytes)
        } catch (exception) {
            digestBytes = null
            exception.printStackTrace()
        }

        if (digestBytes == null) {
            return null
        }

        try (var writer = Files.newBufferedWriter(checksumFile.toPath())) {
            writer.write(sha256ToHex(digestBytes))
        } catch (exception) {
            exception.printStackTrace()
            return null
        }

        return checksumFile
    }

    static String sha256ToHex(byte[] digestBytes) {
        var builder = new StringBuilder(digestBytes.length * 2)
        for (var b in digestBytes) {
            builder.append(Integer.toHexString(0xFF & b)
                    .padLeft(1, "0"))
        }
        return builder.toString()
    }

    static List<File> listLibraries(File file) {
        var list = new LinkedList<File>()
        for (var libraryFile in file.listFiles()) {
            if (!libraryFile.name.endsWithIgnoreCase(".jar")) {
                continue
            }
            list += libraryFile
        }
        return list
    }
}
