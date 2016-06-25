package net.walterbarnes.statusbot.handler;

import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.StatusBot;
import net.walterbarnes.statusbot.command.Commands;
import net.walterbarnes.statusbot.event.MessageReceivedEvent;
import net.walterbarnes.statusbot.eventhandler.SubscribeEvent;
import net.walterbarnes.statusbot.util.LogHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityHandler {

    @SubscribeEvent
    public void onGroupMessage(MessageReceivedEvent.Group event) {
        checkMemos(event.bot, event.from);
        System.out.println(event.msg.getToType());
        LogHelper.info("Checking message for command");
        if (event.message.startsWith("!debug"))
        {
            event.bot.reply("Skype name: " + event.from + " Display name: " + event.fromDisplayName);
        }
        if (Commands.isCommand(event.message, event.to)) {
            LogHelper.info("Command found");
            String rep = Commands.exec(event.bot, event.from, event.to, event.message);
            if (rep.length() > 300)
            {
                event.bot.send(event.from, rep, false);
                event.bot.reply(event.from.split(":", 2)[1] + " check your private messages, if you don't see anything, add me as a contact and try again", true);
            }
            else
            {
                event.bot.reply(rep, false);
            }
        }
    }

    @SubscribeEvent
    public void onPersonalMessage(MessageReceivedEvent.Personal event) {
        checkMemos(event.bot, event.from);
        System.out.println(event.msg.getToType());
        if (Commands.isCommand(event.message, event.from)) {
            event.bot.reply(Commands.exec(event.bot, event.from, event.from, event.message));
        }
    }

    private void checkMemos(Bot bot, String user) {
        try {
            StatusBot.checkMemos.setString(1, user.split(":", 2)[1]);
            try (ResultSet rs = StatusBot.checkMemos.executeQuery()) {
                while (rs.next()) {
                    bot.reply(String.format("Memo to '%s' from '%s'%n%s", user, rs.getString("fromuser"), rs.getString("message")));
                    StatusBot.delMemo.setInt(1, rs.getInt("id"));
                    StatusBot.delMemo.execute();
                }
            }
        } catch (SQLException e) {
            LogHelper.error(e);
            bot.reply("Unknown error occurred, check the log for details");
        }
    }
}
