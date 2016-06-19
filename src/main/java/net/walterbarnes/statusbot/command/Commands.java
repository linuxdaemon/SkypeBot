package net.walterbarnes.statusbot.command;

import net.walterbarnes.skypesdk.messaging.Bot;
import net.walterbarnes.statusbot.util.LogHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Commands {
    public static final Map<String, Command> commandList = new LinkedHashMap<>();
    public static final Map<String, Command> immutableCommandList = new LinkedHashMap<>();

    public static final Map<String, Map<String, Command>> channelCommandList = new LinkedHashMap<>();
    public static final Map<String, Map<String, Command>> immutableChannelCommandList = new LinkedHashMap<>();

    public static void registerGlobalCommand(String command, Command commandClass, boolean removable) {
        if (removable) {
            commandList.put(command, commandClass);
        } else {
            immutableCommandList.put(command, commandClass);
        }
    }

    public static void registerCommand(String channel, String command, Command commandClass, boolean removable) {
        if (removable) {
            if (!(channelCommandList.containsKey(channel) && channelCommandList.get(channel) != null)) {
                channelCommandList.put(channel, new HashMap<>());
            }
            channelCommandList.get(channel).put(command, commandClass);
        } else {
            if (!(immutableChannelCommandList.containsKey(channel) && immutableChannelCommandList.get(channel) != null)) {
                immutableChannelCommandList.put(channel, new HashMap<>());
            }
            immutableChannelCommandList.get(channel).put(command, commandClass);
        }
    }

    public static boolean isCommand(String text, String channel) {
        //System.out.println(channelCommandList);
        String cmd = text.split(" |\n")[0].toLowerCase();
        return commandList.containsKey(cmd) || immutableCommandList.containsKey(cmd) || channelCommandList.size() > 0 && channelCommandList.containsKey(channel) && channelCommandList.get(channel).containsKey(cmd) || immutableChannelCommandList.size() > 0 && immutableChannelCommandList.containsKey(channel) && immutableChannelCommandList.get(channel).containsKey(cmd);
    }

    public static boolean isCommandEditable(String text, String channel) {
        //System.out.println(channelCommandList);
        String cmd = text.split(" |\n")[0].toLowerCase();
        return commandList.containsKey(cmd) || (channelCommandList.size() > 0 && channelCommandList.containsKey(channel) && channelCommandList.get(channel).containsKey(cmd));
    }

    public static Command getCommand(String channel, String text) {
        String command = text.split(" |\n", 2)[0].toLowerCase();
        Command cmd;
        if (commandList.containsKey(command)) {
            cmd = commandList.get(command);
        } else if (immutableCommandList.containsKey(command)) {
            cmd = immutableCommandList.get(command);
        } else if (channelCommandList.get(channel).containsKey(command)) {
            cmd = channelCommandList.get(channel).get(command);
        } else if (immutableChannelCommandList.get(channel).containsKey(command)) {
            cmd = immutableChannelCommandList.get(channel).get(command);
        } else {
            LogHelper.warn(String.format("Unregistered command: %s", text));
            return null;
        }
        return cmd;
    }

    public static String exec(Bot bot, String user, String channel, String text) {
        Command command = getCommand(channel, text);
        if (command == null) {
            return "";
        }
        return command.exec(bot, user, channel, text.split(" |\n"));
    }
}
