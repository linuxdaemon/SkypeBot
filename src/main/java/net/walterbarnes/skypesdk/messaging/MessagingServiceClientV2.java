package net.walterbarnes.skypesdk.messaging;

public class MessagingServiceClientV2 {
    private final String appId, appSecret;
    private final String serverUrl;
    private final int requestTimeout;
    private final TokenService tokenService;

    public MessagingServiceClientV2(String appId, String appSecret, String serverUrl, int requestTimeout) {
        this.appId = appId.trim();
        this.appSecret = appSecret.trim();
        this.serverUrl = serverUrl.trim();
        this.requestTimeout = requestTimeout;
        this.tokenService = new TokenService(this.appId, this.appSecret);
    }
}
