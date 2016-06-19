package net.walterbarnes.skypesdk.messaging;

import java.util.Date;

public class TokenService {
    private final String appId;
    private final String appSecret;
    private final Date validUntil;
    private final String token;
    private final boolean renewingToken;
    private final int renewBeforeExpiration;
    private final String scope;
    private final String oAuthUrl;

    public TokenService(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;

        this.token = null;
        this.validUntil = new Date(0);
        this.renewingToken = false;
        this.renewBeforeExpiration = 600;
        this.scope = "https://graph.microsoft.com/.default";
        this.oAuthUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/token";

    }
}
