package net.walterbarnes.statusbot.command;

import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.StatusBot;
import net.walterbarnes.statusbot.init.RegisterCommands;
import net.walterbarnes.statusbot.util.LogHelper;
import net.walterbarnes.statusbot.util.MessageHelper;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class CommandEdit extends Command {
    public CommandEdit() {
        super();
        this.setHelpMsg("Allows you to edit existing commands");
        this.setNumArgs(2);
    }

    @Override
    public String run(Bot bot, String user, String channel, String... args) {
        String cmd = args[1].toLowerCase();
        if (Commands.isCommandEditable(cmd, channel)) {
            String reply = StringUtils.join(Arrays.asList(args).subList(2, args.length), " ");
            //Commands.registerCommand(channel, cmd, new BasicCommand().setReply(reply).shouldParse(true), true);
            try (PreparedStatement stmt = StatusBot.fdb.prepareStatement("UPDATE new_commands SET reply = ? WHERE trigger = ? AND channel = ?")) {
                stmt.setString(1, reply);
                stmt.setString(2, cmd);
                stmt.setString(3, channel);
                stmt.execute();
            } catch (SQLException e) {
                LogHelper.error(e);
                return "An unknown error occurred, check log for details.";
            }
            RegisterCommands.load();
            return MessageHelper.User.getUserNameFromString(user) + " edited command " + cmd;
        }
        return "Command '" + cmd + "' does not exist, or cannot be edited";
    }
}
