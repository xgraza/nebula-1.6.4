package us.nebula.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class DownloadMCAssets extends DefaultTask
{
    private static final String LEGACY_ASSETS_URL = "https://launchermeta.mojang.com/v1/packages/770572e819335b6c0a053f8378ad88eda189fc14/legacy.json"
// https://resources.download.minecraft.net/

    @Input String runDirectory

    @TaskAction
    void run()
    {
        var runFolder = new File(runDirectory)
        if (!runFolder.exists() || !runFolder.isDirectory())
        {
            println "You must create a run folder at $runDirectory"
            return
        }

        var assetsFolder = new File(runFolder, "assets")
        if (assetsFolder.exists())
        {
            println "Removing old assets folder at $assetsFolder"
            if (!assetsFolder.delete())
            {
                println "Failed to delete old assets folder!"
                return
            }
            println "Removed old assets folder!"
        }
        println "Creating new assets folder at $assetsFolder"
        if (!assetsFolder.mkdir())
        {
            println "Could not create new assets folder"
        }

    }
}
