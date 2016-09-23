package com.dmo.mike.relay.auth;

import java.security.KeyPair;
import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableResourceServer
@EnableAuthorizationServer
public class DemoAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoAuthApplication.class, args);
	}


	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	  @Configuration
	  protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

	    @Bean
	    public JwtAccessTokenConverter jwtAccessTokenConverter() {
	      JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
	      KeyPair keyPair = new KeyStoreKeyFactory(
	          new ClassPathResource("keystore.jks"), "foobar".toCharArray())
	          .getKeyPair("test");
	      converter.setKeyPair(keyPair);
	      return converter;
	    }

	    @Override
	    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory().withClient("foo")
			.secret("foosecret")
			.authorizedGrantTypes("client_credentials")
			.scopes("openid")
			.accessTokenValiditySeconds(6000);

	    }

	    @Override
	    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
	        throws Exception {
	      endpoints
	               .accessTokenConverter(jwtAccessTokenConverter());
	    }

	    @Override
	    public void configure(AuthorizationServerSecurityConfigurer oauthServer)
	        throws Exception {
	      oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess(
	          "isAuthenticated()");
	    }
	  }
}
