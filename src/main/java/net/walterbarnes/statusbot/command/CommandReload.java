package net.walterbarnes.statusbot.command;

import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.init.RegisterCommands;

public class CommandReload extends Command {
    public CommandReload() {
        super();
        this.setHelpMsg("Reloads commands from configuration file");
    }

    @Override
    public String run(Bot bot, String user, String channel, String... args) {
        RegisterCommands.load();
        return "Reload complete";
    }
}
