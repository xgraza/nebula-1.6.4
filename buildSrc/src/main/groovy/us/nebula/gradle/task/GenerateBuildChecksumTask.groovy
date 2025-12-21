package us.nebula.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import us.nebula.gradle.Util

/**
 * @author xgraza
 * @since 1.0.0
 */
class GenerateBuildChecksumTask extends DefaultTask {
    @Input
    String buildDirectory

    @TaskAction
    void run() {
        var jarFile = findJarFile()
        if (jarFile == null) {
            println "ERROR!! No jar file found in build directory"
            return
        }
        println "Creating checksum file for $jarFile"
        var checksumFile = Util.generateChecksumFile(jarFile)
        if (checksumFile != null && checksumFile.exists()) {
            println "Created checksum file at $checksumFile"
        } else {
            println "ERROR!! Failed to create checksum file for $jarFile"
        }
    }

    File findJarFile() {
        var file = new File(buildDirectory)
        for (var child in file.listFiles()) {
            if (child.name.endsWithIgnoreCase(".jar")) {
                return child
            }
        }
        return null
    }
}
