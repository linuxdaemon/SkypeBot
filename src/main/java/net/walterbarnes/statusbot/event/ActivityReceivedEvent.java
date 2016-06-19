package net.walterbarnes.statusbot.event;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.walterbarnes.statusbot.eventhandler.Event;
import net.walterbarnes.statusbot.types.Activity;

public class ActivityReceivedEvent extends Event {
    public final Activity msg;
    public final String raw;

    public ActivityReceivedEvent(String data) {
        raw = data;
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        msg = gson.fromJson(parser.parse(data).getAsJsonObject(), Activity.class);
    }
}
