package net.walterbarnes.skypesdk.messaging;

public class MessagingService {
    private final MessagingServiceClientV2 messagingServiceClient;
    private final String botId;

    public MessagingService(String botId, String appId, String appSecret, String serverUrl, int requestTimeout) {
        this.botId = botId;
        messagingServiceClient = new MessagingServiceClientV2(appId, appSecret, serverUrl, requestTimeout);
    }
}
