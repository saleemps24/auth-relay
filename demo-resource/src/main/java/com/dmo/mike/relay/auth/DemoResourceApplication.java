package com.dmo.mike.relay.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
@EnableResourceServer
public class DemoResourceApplication {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DemoResourceApplication.class);

	@Configuration
	protected static class RestTemplateConfiguration {
		@Bean
		@ConfigurationProperties("security.oauth2.client")
		ClientCredentialsResourceDetails clientDetails() {
			return new ClientCredentialsResourceDetails();
		}
		@Bean
		RestTemplate restTemplate(OAuth2ClientContext context,
				ClientCredentialsResourceDetails client) {
			final RestTemplate restTemplate = new OAuth2RestTemplate(client, context);
			return restTemplate;
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoResourceApplication.class, args);
	}

	private static final String url = "http://localhost:9001/resource/relay/";

	private final RestTemplate restTemplate;

	public DemoResourceApplication(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@RequestMapping(value = "/hello/{id}", method = RequestMethod.GET)
	public Demo securedCall(@PathVariable("id") String id) {
		LOGGER.info("securedCall {}", id);
		return restTemplate.getForObject(url + id, Demo.class);
	}

	@GetMapping("/get/{id}")
	public Demo getCall(@PathVariable("id") String id) {
		LOGGER.info("getCall {}", id);
		return restTemplate.getForObject(url + id, Demo.class);
	}

	@RequestMapping(value = "/relay/{id}", method = RequestMethod.GET)
	public Demo relay(@PathVariable("id") String id) {
		LOGGER.info("relay {}", id);
		return new Demo("Secure Hello: " + id);
	}

	public static class Demo {

		public Demo() {
		}

		public Demo(String id) {
			this.id = id;
		}

		private String id;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
}
