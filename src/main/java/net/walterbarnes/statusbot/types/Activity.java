package net.walterbarnes.statusbot.types;

public class Activity {
    private String id;
    private String content;
    private String activity;
    private String from;
    private String to;
    private String time;
    private String fromDisplayName;

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getActivity() {
        return activity;
    }

    public String getFrom() {
        return from;
    }

    public String getFromDisplayName() {
        return fromDisplayName;
    }

    public String getTo() {
        return to;
    }

    public UserType getToType() {
        return UserType.getTypeFromId(Integer.parseInt(to.split(":", 2)[0]));
    }

    public ActivityType getType() {
        return ActivityType.valueOf(getActivity().trim().toUpperCase());
    }

    public String getTime() {
        return time;
    }

    public enum UserType {
        UNKNOWN(-1),
        USER(8),
        HASHEDUSER(29),
        GROUP(19),
        BOT(28);

        private int id;

        UserType(int id) {
            this.id = id;
        }

        public static UserType getTypeFromId(int id) {
            for (UserType userType : UserType.values()) {
                if (userType.id == id) {
                    return userType;
                }
            }
            return UNKNOWN;
        }

        public int getId() {
            return id;
        }
    }

    public enum ActivityType {
        MESSAGE,
        ATTACHMENT,
        CONVERSATIONUPDATE
    }
}
