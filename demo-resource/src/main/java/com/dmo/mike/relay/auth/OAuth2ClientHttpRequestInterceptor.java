package com.dmo.mike.relay.auth;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public class OAuth2ClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_TOKEN_TYPE = "Bearer";
  private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2ClientHttpRequestInterceptor.class);

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    if (request.getHeaders().containsKey(AUTHORIZATION_HEADER)) {
      LOGGER.warn("The Authorization token has been already set");
    } else {
      relayToken(request);
    }
    return execution.execute(request, body);
  }

  private void relayToken(HttpRequest request) {
    String accessToken = getAccessToken();
    if (accessToken == null) {
      LOGGER.warn("Can not obtain existing token for request, if it is a non secured request, ignore.");
    } else {
      LOGGER.info("Constructing Header {} for Token {}", AUTHORIZATION_HEADER, BEARER_TOKEN_TYPE);
      request.getHeaders().add(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, accessToken));
    }
  }

  private String getAccessToken() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof OAuth2Authentication && auth.getDetails() instanceof OAuth2AuthenticationDetails) {
      OAuth2AuthenticationDetails oauth = (OAuth2AuthenticationDetails) auth.getDetails();
      return oauth.getTokenValue();
    }
    return null;
  }
}
