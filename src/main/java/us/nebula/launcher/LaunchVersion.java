/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher;

/**
 * @author xgraza
 * @since 1.0.0
 */
public enum LaunchVersion
{
    STABLE("stable", "nebula-stable.jar"), // topmost, default option
    _2_0_0("2.0.0", "nebula-2.0.0.jar"),
    LATEST("latest", "nebula-latest.jar");

    private final String name, fileName;

    LaunchVersion(final String name, final String fileName)
    {
        this.name = name;
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static LaunchVersion getVersion(final String name)
    {
        for (final LaunchVersion lv : values())
        {
            if (lv.toString().equals(name))
            {
                return lv;
            }
        }
        return null;
    }
}
