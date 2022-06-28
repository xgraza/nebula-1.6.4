package wtf.nebula.impl.command.impl;

import wtf.nebula.impl.command.Command;

import java.util.Arrays;
import java.util.List;

public class Github extends Command {
    public Github() {
        super(Arrays.asList("github", "gh", "repo"), "Tells you the github repo in case of an issue");
    }

    @Override
    public void execute(List<String> args) {
        sendChatMessage("If you are having any issues: make an issue at the GitHub repo.\nhttps://github.com/Sxmurai/nebula-1.6.4");
    }
}
