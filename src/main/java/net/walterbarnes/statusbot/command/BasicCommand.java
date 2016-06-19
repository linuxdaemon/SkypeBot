package net.walterbarnes.statusbot.command;

import net.walterbarnes.skypesdk.messaging.Bot;

public class BasicCommand extends Command {
    private String reply;
    private boolean parse;

    public BasicCommand() {
        this.setReply("This command is not configured, but was called with these arguments: ${@}");
        this.shouldParse(true);
        this.setHelpMsg(null);
    }

    public String run(Bot bot, String user, String channel, String... args) {
        if (this.getNumArgs() > args.length && this.getNumArgs() != 0) {
            return String.format("Error: Too few arguments, at least %d required", this.getNumArgs());
        }
        return parse(this.getReply(), user, args, channel);
    }

    public String getReply() {
        return reply;
    }

    public BasicCommand setReply(String reply) {
        this.reply = reply;
        return this;
    }

    public boolean shouldParse() {
        return parse;
    }

    public BasicCommand shouldParse(boolean parse) {
        this.parse = parse;
        return this;
    }
}
