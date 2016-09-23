package com.dmo.mike.relay.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
@RestController
@EnableResourceServer
@EnableOAuth2Sso
public class DemoResourceApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(DemoResourceApplication.class);
	@Autowired
	UserInfoRestTemplateFactory factory;

	public static void main(String[] args) {
		SpringApplication.run(DemoResourceApplication.class, args);
	}

//	@Autowired
//	HandlerInterceptor tokenRelayRequestInterceptor;

	@Bean
	public RequestMappingHandlerMapping HandlerMapping(HandlerInterceptor tokenRelayRequestInterceptor) {
		RequestMappingHandlerMapping rmhm = new RequestMappingHandlerMapping();
		rmhm.setInterceptors(tokenRelayRequestInterceptor);
		return rmhm;
	}


	private static final String url = "http://localhost:9001/resource/relay/";
	@RequestMapping(value="/hello/{id}", method=RequestMethod.GET)
	public Demo securedCall(@PathVariable("id") String id) {
		LOGGER.info("securedCall {}", id);
		final OAuth2RestTemplate userInfoRestTemplate = factory.getUserInfoRestTemplate();
		return userInfoRestTemplate.getForObject(url+id, Demo.class);
	}

	@GetMapping("/get/{id}")
	public Demo getCall(@PathVariable("id") String id) {
		LOGGER.info("getCall {}", id);
		final OAuth2RestTemplate userInfoRestTemplate = factory.getUserInfoRestTemplate();
		return userInfoRestTemplate.getForObject(url+id, Demo.class);
	}



	@RequestMapping(value="/relay/{id}", method=RequestMethod.GET)
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
