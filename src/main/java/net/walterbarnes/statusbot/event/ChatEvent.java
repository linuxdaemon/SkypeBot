package net.walterbarnes.statusbot.event;

import net.walterbarnes.statusbot.eventhandler.Event;

public class ChatEvent extends Event {
    public final String chatId;

    public ChatEvent(String chatId) {

        //super();
        this.chatId = chatId;
    }
}
