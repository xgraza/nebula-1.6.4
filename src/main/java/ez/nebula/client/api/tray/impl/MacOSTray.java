package ez.nebula.client.api.tray.impl;

import ez.nebula.client.api.tray.ITray;

import java.io.IOException;

public final class MacOSTray implements ITray
{
    @Override
    public void display(String title, String content)
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("display notification");
        builder.append(" \"");
        builder.append(content);
        builder.append("\"");
        builder.append(" ");
        builder.append("with title \"");
        builder.append(title);
        builder.append("\"");

        final ProcessBuilder processBuilder = new ProcessBuilder("osascript", "-e", builder.toString());
        processBuilder.redirectErrorStream(true);
        try
        {
            final Process process = processBuilder.start();
            if (process.waitFor() != 0)
            {
                throw new RuntimeException("Failed to run osascript");
            }
        } catch (IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
