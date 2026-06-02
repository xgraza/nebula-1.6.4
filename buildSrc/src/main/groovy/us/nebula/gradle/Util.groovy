package us.nebula.gradle

import java.nio.file.Files
import java.security.MessageDigest

/**
 * @author xgraza
 * @since 1.0.0
 */
class Util {
    static final String SEPARATOR = System.getProperty("file.separator")
    static final String USER_DIR = System.getProperty("user.dir")
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

    static getOSType() {
        var name = System.getProperty("os.name").toLowerCase()
        if (name.startsWithIgnoreCase("mac")) {
            return OS.OSX
        } else if (name.startsWithIgnoreCase("win")) {
            return OS.WINDOWS
        }
        return OS.UNIX
    }

    static createPathString(String... path) {
        var joiner = new StringJoiner(SEPARATOR)
        for (var pathPart in path) {
            joiner.add(pathPart)
        }
        return joiner.toString()
    }

    static execute(String command, String defaultValue) {
        try {
            return command.execute().text.trim()
        } catch (exception) {
            exception.printStackTrace()
            return defaultValue
        }
    }

    static Iterable<String> getLaunchJVMFlags() {
        return [
                // stolen from the mc launcher - run as close as we can to the launcher
                "-XX:+UnlockExperimentalVMOptions",
                "-XX:+UseG1GC",
                "-XX:G1NewSizePercent=20",
                "-XX:G1ReservePercent=20",
                "-XX:MaxGCPauseMillis=50",
                "-XX:G1HeapRegionSize=32M",
                "-Djava.library.path=${createPathString(USER_DIR, "dependencies", "natives", getOSType().toString().toLowerCase())}"
        ]
    }

    enum OS {
        WINDOWS,
        OSX,
        UNIX
    }
}
