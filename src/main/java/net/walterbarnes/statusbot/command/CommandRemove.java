package net.walterbarnes.statusbot.command;

import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.StatusBot;
import net.walterbarnes.statusbot.init.RegisterCommands;
import net.walterbarnes.statusbot.util.MessageHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandRemove extends Command {
    public CommandRemove() {
        super();
        this.setHelpMsg("Removes commands from the Command Registry");
        this.setNumArgs(1);
    }

    @Override
    public String run(Bot bot, String user, String channel, String... args) {
        String trg = args[1].toLowerCase();
        if (Commands.isCommandEditable(trg, channel)) {
            try (PreparedStatement stmt = StatusBot.fdb.prepareStatement("DELETE FROM new_commands WHERE trigger = ? AND channel = ?")) {
                stmt.setString(1, trg);
                stmt.setString(2, channel);
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                return "An unknown error occurred, check log for details.";
            }
            RegisterCommands.load();
            return MessageHelper.User.getUserNameFromString(user) + " removed command " + trg;
        }
        return "Command '" + trg + "' does not exist, or cannot be removed";
    }
}
