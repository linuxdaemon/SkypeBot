package net.walterbarnes.statusbot.util;


public final class MessageHelper {
    public static final class User {
        public static final String getUserNameFromString(String s) {
            return s.split(":", 2)[1];
        }
    }
}
