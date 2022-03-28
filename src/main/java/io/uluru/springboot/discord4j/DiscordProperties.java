package io.uluru.springboot.discord4j;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discord")
public class DiscordProperties {

	private String token;
	
	public void setToken(String token) {
		this.token = token;
	}
	public String getToken() {
		return this.token;
	}
	
}
