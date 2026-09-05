package us.nebula.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.charset.Charset
import java.nio.file.Files

class GenerateChangelogs extends DefaultTask
{
    private static final String COMMAND = "git log @{u}.. --oneline"

    @TaskAction
    void run()
    {
        var result = Util.execute(COMMAND, "")
        if (result == null || result.isEmpty())
        {
            println "Failed to fetch!"
            return
        }

        var file = new File(Util.createPathString(Util.USER_DIR, "CHANGELOG.md"))
        if (!file.exists())
        {
            println "CHANGELOG.md does not exist"
            return
        }

        var lines = result.split("\n")
        var builder = new StringBuilder("```diff\n")

        var lastCommit = ""

        for (var line in lines)
        {
            var parts = line.trim().split(" ")
            if (parts.length == 0)
            {
                continue
            }
            var hash = parts[0]
            var type = parts[1]
            if (!type.endsWith(":"))
            {
                println "Weird commit message: $line"
                continue
            }
            if (type.equalsIgnoreCase("docs:")
                    || type.equalsIgnoreCase("test:")
                    || type.equalsIgnoreCase("style:")
                    || type.equalsIgnoreCase("docs"))
            {
                continue
            }

            lastCommit = hash
            parts[0] = ""
            parts[1] = ""

            if (line.containsIgnoreCase("remove")
                    || line.containsIgnoreCase("rm")
                    || line.containsIgnoreCase("del")
                    || line.containsIgnoreCase("delete"))
            {
                builder.append("-")
            } else
            {
                builder.append("+")
            }
            builder.append(" ")
            builder.append(String.join(" ", parts).trim())
            builder.append("\n")
        }
        builder.append("```")

        var str = "Nebula 4.0.0 " + lastCommit + "\n\n" + builder.toString()

        var fileContents = Files.readAllLines(file.toPath())
        var dataList = new ArrayList<String>()
        dataList.add(str)
        dataList.add("---")
        dataList.addAll(fileContents)

        var data = String.join("\n", dataList).trim().getBytes(Charset.defaultCharset())

        Files.write(file.toPath(), data)
    }
}
