package com.github.scribejava.apis.service;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.AbstractRequest;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.oauth.OAuth20Service;

public class SkypeOAuthServiceImpl extends OAuth20Service {
    /**
     * Default constructor
     *
     * @param api    OAuth2.0 api information
     * @param config OAuth 2.0 configuration param object
     */
    public SkypeOAuthServiceImpl(DefaultApi20 api, OAuthConfig config) {
        super(api, config);
    }

    @Override
    protected <T extends AbstractRequest> T createAccessTokenRequest(String oauthVerifier, T request) {
        final OAuthConfig config = getConfig();
        request.addParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
        request.addParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
        request.addParameter(OAuthConstants.SCOPE, config.getScope());
        request.addParameter(OAuthConstants.GRANT_TYPE, "client_credentials");

        return request;
    }

    @Override
    public void signRequest(OAuth2AccessToken accessToken, AbstractRequest request) {
        request.addHeader("Authorization", "Bearer " + accessToken.getAccessToken());
    }
}
