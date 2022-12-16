package wtf.nebula.client.impl.command.impl;

import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.Argument;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.StringArgument;
import wtf.nebula.client.impl.module.active.HUD;

public class Watermark extends Command {
    public Watermark() {
        super(new String[]{"watermark", "displayname"},
                new StringArgument("watermark"));
    }

    @Override
    public String dispatch(CommandContext ctx) {
        HUD.WATERMARK = (String) ctx.get("watermark").getValue();
        return "Set watermark.";
    }
}
