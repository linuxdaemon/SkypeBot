package net.walterbarnes.statusbot.command;

import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.config.DB;
import net.walterbarnes.statusbot.config.types.BlogConfig;
import net.walterbarnes.statusbot.tumblr.Tumblr;
import net.walterbarnes.statusbot.util.LogHelper;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.walterbarnes.statusbot.StatusBot.conf;

public class CommandBuffer extends Command {
    public CommandBuffer() {
        super();
        setHelpMsg("Retrieves buffer information for all registered blogs");
    }

    @Override
    public String run(Bot bot, String user, String channel, String... args) {
        List<String> out = new ArrayList<>();
        Tumblr client = new Tumblr(conf.consumerKey,
                conf.consumerSecret,
                conf.token,
                conf.tokenSecret);
        try (DB db = new DB(client, conf.dbHost, Integer.parseInt(conf.dbPort), conf.dbName, conf.dbUser, conf.dbPass)) {
            db.setDriver("org.postgresql.Driver").setScheme("jdbc:postgresql").connect();
            List<BlogConfig> blogs = db.getAllBlogs();
            blogs.stream().filter(BlogConfig::isActive).forEach(b ->
                    out.add(String.format("%s: %s/%s posts", b.getUrl(), b.getPostBufSize(), b.getPostBuffer())));
        } catch (SQLException e) {
            LogHelper.error(e);
        }
        return out.size() > 0 ? StringUtils.join(out, "\n") : "Unknown error occurred, check the log for details";
    }
}
