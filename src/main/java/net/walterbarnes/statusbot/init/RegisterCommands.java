package net.walterbarnes.statusbot.init;

import com.tumblr.jumblr.types.AnswerPost;
import com.tumblr.jumblr.types.Post;
import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.StatusBot;
import net.walterbarnes.statusbot.command.*;
import net.walterbarnes.statusbot.tumblr.Tumblr;
import net.walterbarnes.statusbot.util.LogHelper;
import net.walterbarnes.statusbot.util.MessageHelper;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class RegisterCommands {
    public static void load() {
        Commands.channelCommandList.clear();
        Commands.commandList.clear();
        Commands.immutableChannelCommandList.clear();
        Commands.immutableCommandList.clear();
        /**                      GLOBAL COMMANDS                                */
        Commands.registerGlobalCommand("!addcom", new CommandAdd(), false);
        Commands.registerGlobalCommand("!editcom", new CommandEdit(), false);
        Commands.registerGlobalCommand("!delcom", new CommandRemove(), false);
        Commands.registerGlobalCommand("!relcom", new CommandReload(), false);
        Commands.registerGlobalCommand("!help", new CommandHelp(), false);

        Commands.registerGlobalCommand("!memo", new Command() {
            @Override
            public String run(Bot bot, String user, String channel, String... args) {
                String toUser = args[1];
                String message = StringUtils.join(Arrays.asList(args).subList(2, args.length), " ");
                try {
                    StatusBot.addMemo.setString(1, MessageHelper.User.getUserNameFromString(user));
                    StatusBot.addMemo.setString(2, toUser);
                    StatusBot.addMemo.setString(3, message);
                    StatusBot.addMemo.execute();
                } catch (SQLException e) {
                    LogHelper.error(e);
                    return "An unknown error occurred, check the log for details";
                }
                return "Memo saved for user '" + toUser + "'";
            }
        }.setHelpMsg("Creates a memo to a certain user").setNumArgs(2), false);

        Commands.registerGlobalCommand("!ping", new Command() {
            @Override
            public String run(Bot bot, String user, String channel, String... args) {
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                String t = new SimpleDateFormat("HH:mm:ss").format(timestamp);
                String d = new SimpleDateFormat("dd/MM/yyyy").format(timestamp);
                String s = t + " on " + d;
                return "It is currently " + s;
            }
        }.setHelpMsg("Returns the current time (for debugging purposes)"), false);


        Commands.registerGlobalCommand("!getraw", new Command() {
            @Override
            public String run(Bot bot, String user, String channel, String... args) {
                String cmd = args[1];
                String ret = "";

                Command cmdClass;
                if (Commands.isCommand(cmd, channel) && (cmdClass = Commands.getCommand(channel, cmd)) instanceof BasicCommand)
                {
                    BasicCommand bscCmd = (BasicCommand) cmdClass;
                    return bscCmd.getReply();
                }
                else
                {
                    return String.format("Command '%s' is not available or has no raw output", cmd);
                }
            }
        }.setHelpMsg("Gets the raw unparsed version of a command").setNumArgs(1), false);


        /**                                     PC CHAT COMMANDS                                                                 */


        Commands.registerCommand("19:b411530ec0184c9ca433fe67f36799b9@thread.skype", "!blog", new CommandBlogConfig(), false);
        Commands.registerCommand("19:b411530ec0184c9ca433fe67f36799b9@thread.skype", "!buffer", new CommandBuffer(), false);

//        Commands.registerCommand("19:b411530ec0184c9ca433fe67f36799b9@thread.skype", "!cpd", new Command() {
//            @Override
//            public String run(Bot bot, String user, String channel, String... args) {
//                Tumblr client = new Tumblr("6phz4ddZhX17mvGbTJcvhXH7MaE2hjdyNGu8qFtJPPd17HWgn1",
//                        "9gskxfMCKhsIDiYy9bjssK8V5sbaYR1c6Ga2pZQcKqeHIzT8QL",
//                        "2c9X5ZZ5EWFcZEbam2UDWztgyEMMTHTmN1kZX3B4r6cOrMsfrw",
//                        "WpKer6hSp7Y1jN0VxeLfGOtGvhMAcGDEcc1rcldrPRe76AqpiA");
//
//                Map<String, Object> params = new HashMap<>();
//                params.put("type", "photo");
//                params.put("limit", 1);
//                List<Post> posts = client.blogPosts("cutepuppyoftheday.tumblr.com", params);
//                PhotoPost post = (PhotoPost) posts.get(0);
//                bot.replyWithAttachment();
//                return post.getPhotos().get(0).getOriginalSize().getUrl();
//                //client.getBlogPosts("cutepuppyoftheday")
//            }
//        }, false);

        Commands.registerGlobalCommand("!skylar", new Command() {
            @Override
            public String run(Bot bot, String user, String channel, String... args) {
                Tumblr client = new Tumblr(StatusBot.conf.consumerKey,
                        StatusBot.conf.consumerSecret,
                        StatusBot.conf.token,
                        StatusBot.conf.tokenSecret);

                Map<String, Object> params = new HashMap<>();
                params.put("type", "answer");
                params.put("limit", 1);
                params.put("filter", "text");
                List<Post> posts = client.blogPosts("projectbot13.tumblr.com", params);
                AnswerPost post = (AnswerPost) posts.get(0);
                return String.format("Ask from %s:%n%s%nSkylar's response:%n%s", post.getAskingName(), post.getQuestion(), post.getAnswer());
            }
        }.setHelpMsg("Gets the most recent post from Skylar (projectbot13.tumblr.com)"), false);


        /**                        REGISTER COMMANDS FROM DATABASE                                         */


        try (PreparedStatement comPs = StatusBot.fdb.prepareStatement("SELECT trigger,reply,parse,channel FROM new_commands")) {
            try (ResultSet rs = comPs.executeQuery()) {
                while (rs.next()) {
                    String trg = rs.getString("trigger");
                    String rpl = rs.getString("reply");
                    boolean prs = rs.getBoolean("parse");
                    String ch = rs.getString("channel");
                    //System.out.printf("t: %s, r: %s, p: %s, c: %s%n", trg, rpl, prs, ch);
                    if (ch == null) {
                        Commands.registerGlobalCommand(trg, new BasicCommand().setReply(rpl).shouldParse(prs), false);
                    } else {
                        Commands.registerCommand(ch, trg, new BasicCommand().setReply(rpl).shouldParse(prs), true);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
