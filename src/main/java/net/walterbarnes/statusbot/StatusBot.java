package net.walterbarnes.statusbot;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.walterbarnes.statusbot.config.Configuration;
import net.walterbarnes.statusbot.config.DB;
import net.walterbarnes.statusbot.event.MessageReceivedEvent;
import net.walterbarnes.statusbot.eventhandler.EventPublisher;
import net.walterbarnes.statusbot.eventhandler.HandlerRegistry;
import net.walterbarnes.statusbot.handler.ActivityHandler;
import net.walterbarnes.statusbot.init.RegisterCommands;
import net.walterbarnes.statusbot.tumblr.Tumblr;
import net.walterbarnes.statusbot.types.Activity;
import net.walterbarnes.statusbot.util.LogHelper;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class StatusBot {
    public static final StatusBot INSTANCE = new StatusBot();
    private static final Gson gson = new Gson();
    private static final JsonParser parser = new JsonParser();
    public static Connection fdb;
    public static Connection sbdb;
    public static PreparedStatement saveReplyToDb;
    public static Configuration conf;
    public static PreparedStatement checkMemos;
    public static PreparedStatement addMemo;
    public static PreparedStatement delMemo;
    public static DB db;
    private static Tumblr client;

    static {
        try {
            conf = new Configuration(INSTANCE.confDir.getAbsolutePath(), INSTANCE.confName);
            Class.forName("org.postgresql.Driver").newInstance();
            client = new Tumblr(conf.consumerKey,
                    conf.consumerSecret,
                    conf.token,
                    conf.tokenSecret);
            db = new DB(client, conf.dbHost, Integer.parseInt(conf.dbPort), conf.dbName, conf.dbUser, conf.dbPass);
            db.setDriver("org.postgresql.Driver").setScheme("jdbc:postgresql").connect();
            sbdb = DriverManager.getConnection(String.format("%s://%s:%s/%s", "jdbc:postgresql", conf.dbHost, conf.dbPort, conf.dbName), conf.dbUser, conf.dbPass);
            fdb = DriverManager.getConnection(String.format("%s://%s:%s/%s", "jdbc:postgresql", conf.dbHost, conf.dbPort, "fawkes"), conf.dbUser, conf.dbPass);
            saveReplyToDb = fdb.prepareStatement("INSERT INTO reply_queue (toid, msg, esc, type) VALUES (?,?,?,?)");
            checkMemos = fdb.prepareStatement("SELECT id,fromuser,message FROM memos WHERE touser = ?");
            addMemo = fdb.prepareStatement("INSERT INTO memos (fromuser, touser, message) VALUES (?,?,?)");
            delMemo = fdb.prepareStatement("DELETE FROM memos WHERE id = ?");
        } catch (SQLException | IllegalAccessException | InstantiationException | ClassNotFoundException | FileNotFoundException e) {
            LogHelper.error(e);
            System.exit(1);
        }
    }

    public final String confName = "SourceBot.json";
    public File confDir = new File(System.getProperty("user.home"), ".sourcebot");

    public static void main(String[] args) throws Exception {
        INSTANCE.run();
    }

    public void run() throws Exception {
        RegisterCommands.load();
        HandlerRegistry.register(ActivityHandler.class);
        Class.forName("org.postgresql.Driver").newInstance();
        String keystorePath = System.getenv("JETTY_KEYSTORE");
        String ks_pass = System.getenv("JETTY_KEYSTORE_PASSWORD");
        String km_pass = System.getenv("JETTY_KEYMANAGER_PASSWORD");
        File keystoreFile = new File(keystorePath);
        if (!keystoreFile.exists()) {
            throw new FileNotFoundException(keystoreFile.getAbsolutePath());
        }

        Server server = new Server();
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);

        ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(http_config));
        http.setPort(8080);
        http.setIdleTimeout(30000);

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
        sslContextFactory.setKeyStorePassword(ks_pass);
        sslContextFactory.setKeyManagerPassword(km_pass);

        HttpConfiguration https_config = new HttpConfiguration(http_config);
        SecureRequestCustomizer src = new SecureRequestCustomizer();
        src.setStsMaxAge(2000);
        src.setStsIncludeSubDomains(true);
        https_config.addCustomizer(src);

        ServerConnector https = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        https.setPort(8443);
        https.setIdleTimeout(500000);

        server.setConnectors(new Connector[]{http, https});

        server.setHandler(new Handler());

        server.start();
        server.join();
    }

    private static class Handler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            Scanner br = new Scanner(baseRequest.getInputStream());
            String line;
            String msg = "";
            while (br.hasNextLine() && (line = br.nextLine()) != null) {
                msg += line;
            }
            LogHelper.debug(msg);
            Activity[] actArr = gson.fromJson(parser.parse(msg), Activity[].class);
            for (Activity act : actArr) {
                String raw = gson.toJson(act);
                switch (act.getType()) {
                    case MESSAGE:
                        switch (act.getToType()) {
                            case GROUP:
                                LogHelper.info("Event raised");
                                EventPublisher.raiseEvent(new MessageReceivedEvent.Group(raw));
                                break;

                            case BOT:
                                LogHelper.info("Event raised");
                                EventPublisher.raiseEvent(new MessageReceivedEvent.Personal(raw));
                                break;

                            default:
                                System.err.println("Unexpected user type: " + act.getTo());
                        }
                        break;

                    case ATTACHMENT:
                        LogHelper.debug("Attachment received");
                        LogHelper.debug(raw);
                        break;

                    case CONVERSATIONUPDATE:
                        LogHelper.debug("Conversation Update received");
                        LogHelper.debug(raw);
                        break;

                    default:
                        System.err.println("Unexpected activity type: " + act.getActivity());
                }
            }
            baseRequest.setHandled(true);
        }
    }
}
