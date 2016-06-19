package net.walterbarnes.skypesdk;

import net.walterbarnes.skypesdk.messaging.MessagingService;

public class BotService {
    private MessagingService messagingService = null;
    private String messagingBotId;
    private String messagingAppId, messagingAppSecret;
    private int requestTimeout;
    private String serverUrl;

    public BotService buildMessaging() {
        messagingService = new MessagingService(messagingBotId, messagingAppId, messagingAppSecret, serverUrl, requestTimeout);
        return this;
    }

    public BotService setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public BotService setMessagingBotId(String messagingBotId) {
        this.messagingBotId = messagingBotId;
        return this;
    }

    public BotService setMessagingAppId(String messagingAppId) {
        this.messagingAppId = messagingAppId;
        return this;
    }

    public BotService setMessagingAppSecret(String messagingAppSecret) {
        this.messagingAppSecret = messagingAppSecret;
        return this;
    }

    public BotService setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }
}
