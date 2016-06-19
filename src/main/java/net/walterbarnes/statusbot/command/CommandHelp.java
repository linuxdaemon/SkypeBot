package net.walterbarnes.statusbot.command;

import net.walterbarnes.skypesdk.messaging.Bot;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandHelp extends Command {
    public CommandHelp() {
        super();
        this.setHelpMsg("Returns a list of all currently registered commands with their respective descriptions");
    }

    @Override
    public String run(Bot bot, String user, String channel, String... args) {
        Map<String, Command> cmdList = new LinkedHashMap<>();
        cmdList.putAll(Commands.immutableCommandList);
        cmdList.putAll(Commands.commandList);

        if (Commands.immutableChannelCommandList.containsKey(channel)) {
            cmdList.putAll(Commands.immutableChannelCommandList.get(channel));
        }
        if (Commands.channelCommandList.containsKey(channel)) {
            cmdList.putAll(Commands.channelCommandList.get(channel));
        }

        ArrayList<String> out = new ArrayList<>();
        //System.out.println(cmdList.size());
        if (cmdList.isEmpty() || cmdList.size() == 0) {
            out.add("No commands currently registered");
        } else {
            cmdList.entrySet().stream().filter(e -> e.getValue().getHelpMsg() != null).forEachOrdered(e -> {
                out.add(String.format("%s: %s", e.getKey(), e.getValue().getHelpMsg()));
            });
            //String custLine = "Custom Commands: ";
            ArrayList<String> custArr = new ArrayList<>();
            cmdList.entrySet().stream().filter(e -> e.getValue().
                    getHelpMsg() == null).forEachOrdered(e -> {
                custArr.add(e.getKey());
            });
            if (custArr.size() > 0) {
                out.add(String.format("Custom Command%s: %s",
                        custArr.size() > 1 ? "s" : "", StringUtils.join(custArr, ", ")));
            }
        }

        bot.send(user, StringUtils.join(out, "\n"), true);
        return "";
    }
}
