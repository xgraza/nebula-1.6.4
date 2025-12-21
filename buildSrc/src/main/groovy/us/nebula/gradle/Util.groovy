package us.nebula.gradle

/**
 * @author xgraza
 * @since 1.0.0
 */
class Util {
    static final String SEPARATOR = System.getProperty("file.separator")
    static final String USER_DIR = System.getProperty("user.dir")

    static getOSType() {
        var name = System.getProperty("os.name").toLowerCase()
        if (name.startsWithIgnoreCase("mac")) {
            return OS.MAC
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

    static execute(String command) {
        try {
            return command.execute().text.trim()
        } catch (exception) {
            exception.printStackTrace()
            return null
        }
    }

    static Iterable<String> getLaunchJVMFlags() {
        // stolen from the mc launcher - run as close as we can to the launcher
        var args = [
                "-XX:+UnlockExperimentalVMOptions",
                "-XX:+UseG1GC",
                "-XX:G1NewSizePercent=20",
                "-XX:G1ReservePercent=20",
                "-XX:MaxGCPauseMillis=50",
                "-XX:G1HeapRegionSize=32M"
        ]

        var natives = createPathString(Util.USER_DIR, "dependencies")
        switch (getOSType()) {
            case OS.MAC: {
                // this prevents a crash in a dylib where it creates a window in a different thread...
                //args.add("-XstartOnFirstThread")
                natives = createPathString(natives, "macos", "natives")
                break
            }
            case OS.WINDOWS: {
                natives = createPathString(natives, "windows")
                break
            }
            case OS.UNIX: {
                break
            }
        }

        args.add("-Djava.library.path=$natives")
        return args
    }

    enum OS {
        WINDOWS,
        MAC,
        UNIX
    }
}
