package net.walterbarnes.skypesdk.messaging;

import net.walterbarnes.statusbot.StatusBot;
import net.walterbarnes.statusbot.util.LogHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Bot {

    private String replyTo;

    public Bot(String replyTo) {
        this.replyTo = replyTo;
    }

    public void reply(String content) {
        reply(content, false);
    }

    public void reply(String content, boolean escape) {
        send(this.replyTo, content, escape);
    }

    public void send(String user, String content, boolean escape) {
        LogHelper.debug("Sending message to " + user);
        if (content == null || content.isEmpty() || content.trim().equals("")) {
            LogHelper.warn("Empty content");
            return;
        }
        try {
            PreparedStatement ps = StatusBot.saveReplyToDb;
            ps.setString(1, user);
            ps.setString(2, content);
            ps.setBoolean(3, escape);
            ps.setString(4, "message");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database Error occurred");
        }
    }

    public void replyWithAttachment(String name, AttachmentType type, String content) {

    }

    public enum AttachmentType {
        IMAGE,
        VIDEO
    }
}
