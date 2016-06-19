package net.walterbarnes.statusbot.command;

import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.StatusBot;
import net.walterbarnes.statusbot.util.LogHelper;
import net.walterbarnes.statusbot.util.MessageHelper;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class CommandAdd extends Command {
    public CommandAdd() {
        super();
        this.setHelpMsg("Adds basic commands to the Command Registry");
        this.setNumArgs(2);
    }

    @Override
    public String run(Bot bot, String user, String channel, String... args) {
        String trg = args[1].toLowerCase();
        if (!Commands.isCommand(trg, channel)) {
            String reply = StringUtils.join(Arrays.asList(args).subList(2, args.length), " ");
            Commands.registerCommand(channel, trg, new BasicCommand().setReply(reply).shouldParse(true), true);
            try (PreparedStatement stmt = StatusBot.fdb.prepareStatement("INSERT INTO new_commands (trigger, reply, created_by, channel) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, trg);
                stmt.setString(2, reply);
                stmt.setString(3, user);
                stmt.setString(4, channel);
                LogHelper.info("Adding command " + trg);
                stmt.execute();
                LogHelper.info("Command added.");
            } catch (SQLException e) {
                LogHelper.error(e);
                return "An unknown error occurred, check log for details.";
            }
            return MessageHelper.User.getUserNameFromString(user) + " added command " + trg;
        }
        return "Command already exists";
    }
}