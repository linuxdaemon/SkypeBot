package net.walterbarnes.statusbot.command;

import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.StatusBot;
import net.walterbarnes.statusbot.config.types.BlogConfig;
import net.walterbarnes.statusbot.util.LogHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CommandBlogConfig extends Command {

    public CommandBlogConfig() {
        super();
        setNumArgs(3);
        setHelpMsg("Allows for changing configuration options for BlogBot");
    }

    @Override
    public String run(Bot bot, String user, String channel, String... args) {
        String url = args[1];
        String action = args[2];
        List<BlogConfig> blogs = StatusBot.db.getAllBlogs();
        boolean valid = false;
        BlogConfig blog = null;
        for (BlogConfig b : blogs) {
            if (b.getUrl().equals(url)) {
                LogHelper.debug("blog found");
                blog = b;
                valid = true;
                break;
            }
        }
        if (!valid) {
            LogHelper.warn("Invalid url");
            return String.format("Invalid url '%s'", url);
        }
        switch (action) {
            case "set":
                if (args.length < 5) {
                    return "Too few arguments for command 'blog'";
                }
                switch (args[3]) {
                    case "active":
                        if (args[4].equals("true") || args[4].equals("false")) {
                            try {
                                PreparedStatement q = StatusBot.sbdb.prepareStatement("UPDATE blogs SET active = ? WHERE url = ?");
                                q.setBoolean(1, Boolean.getBoolean(args[4]));
                                q.setString(2, url);
                                q.execute();
                                return "Config updated";
                            } catch (SQLException e) {
                                LogHelper.error(e);
                                return "Unknown Error occurred, check log for details";
                            }
                        } else {
                            return "Unrecognized option: " + args[4];
                        }
                    case "buffer":
                        try {
                            int i = Integer.parseInt(args[4]);
                            if (i < 250 && i > 0) {
                                PreparedStatement q = StatusBot.sbdb.prepareStatement("UPDATE blogs SET post_buffer = ? WHERE url = ?");
                                q.setInt(1, i);
                                q.setString(2, url);
                                q.execute();
                                return "Updated blog config";
                            }
                            return "Invalid buffer size: " + i;
                        } catch (NumberFormatException e) {
                            return args[4] + " is not a valid number";
                        } catch (SQLException e) {
                            LogHelper.error(e);
                            return "Unknown error occurred, check log for details";
                        }
                    default:
                        return "Unrecognized option: " + args[3];
                }
            case "get":
                LogHelper.debug("getting settings");
                switch (args[3]) {
                    case "active":
                        blog.loadConfig();
                        if (blog.isActive()) {
                            return String.format("Blog '%s' is active", url);
                        }
                        return String.format("Blog '%s' is not active", url);
                    case "buffer":
                        blog.loadConfig();
                        return String.format("Blog '%s' current buffer: %d/%d", url, blog.getBufferSize(), blog.getPostBuffer());
                    default:
                        return "Unrecognized option: " + args[3];
                }
            default:
                return "Unrecognized action: " + action;
        }
    }
}
