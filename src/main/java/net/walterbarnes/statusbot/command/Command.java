package net.walterbarnes.statusbot.command;

import com.github.onyxfoxdevelopment.collections.CollectionHelper;
import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.util.MessageHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Command {
    private String helpMsg = null;
    private int numArgs = 0;

    static String parse(String text, String user, String[] args, String channel) {
        Map<String, String> fmt = new HashMap<>();
        List<String> argList = Arrays.asList(args);
        fmt.put("${@}", args.length > 1 ? StringUtils.join(argList.subList(1, argList.size()), " ") : "");
        fmt.put("${*}", StringUtils.join(argList, " "));
        fmt.put("${+}", StringUtils.join(argList, "+"));
        fmt.put("${u}", MessageHelper.User.getUserNameFromString(user));
        for (int i = 0; i < args.length; i++) {
            fmt.put(String.format("${%d}", i), args[i]);
        }

        for (int i = 0; i < args.length; i++) {
            fmt.put(String.format("${%d-}", i), StringUtils.join(argList.subList(i, argList.size() - 1), " "));
        }

        Pattern pattern = Pattern.compile("\\$\\{([^\\}]+)\\}");
        Matcher matcher = pattern.matcher(text);

        List<String> subs = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                subs.add(matcher.group(i));
            }
        }
        for (String string : subs) {
            if (string.startsWith("random:")) {
                System.out.println(string);
                List<String> posArr = Arrays.asList(string.split(":", 2)[1].split(",", -1));
                System.out.println(posArr);
                ArrayList<String> s = new ArrayList<>(CollectionHelper.randomElement(posArr, 1, true));
                text = text.replaceFirst(String.format("\\$\\{%s\\}", string), s.get(0));
                continue;
            }
            if (!fmt.containsKey(String.format("${%s}", string))) {
                return "Error in command, too few parameters";
            }
            text = text.replaceFirst(String.format("\\$\\{%s\\}", string), fmt.get(String.format("${%s}", string)));
        }
        return text;
    }

    String getHelpMsg() {
        return helpMsg;
    }

    public Command setHelpMsg(String helpMsg) {
        this.helpMsg = helpMsg;
        return this;
    }

    int getNumArgs() {
        return numArgs;
    }

    public Command setNumArgs(int numArgs) {
        this.numArgs = numArgs;
        return this;
    }

    public String exec(Bot bot, String user, String channel, String... args) {
        if (!checkArgs(args)) {
            return String.format("Too few arguments, at least %d expected", numArgs);
        }
        return run(bot, user, channel, args);
    }

    public abstract String run(Bot bot, String user, String channel, String... args);

    public boolean checkArgs(String[] args) {
        return args.length + 1 >= numArgs;
    }
}
