package net.walterbarnes.statusbot.event;

import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.types.Activity;

public class MessageReceivedEvent extends ActivityReceivedEvent {
    public final String from;
    public final String fromDisplayName;
    public final String message;
    public final String to;
    public final Bot bot;
    public final Activity.UserType toType;

    public MessageReceivedEvent(String data) {
        super(data);
        this.message = msg.getContent();
        this.from = msg.getFrom();
        this.fromDisplayName = msg.getFromDisplayName();
        this.to = msg.getTo();
        this.toType = msg.getToType();
        switch (this.toType) {
            case GROUP:
                this.bot = new Bot(this.to);
                break;
            case BOT:
                this.bot = new Bot(this.from);
                break;
            default:
                this.bot = new Bot(null);
                break;
        }
    }

    public static class Group extends MessageReceivedEvent {
        public Group(String data) {
            super(data);
        }
    }

    public static class Personal extends MessageReceivedEvent {
        public Personal(String data) {
            super(data);
        }
    }
}
