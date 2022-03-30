package io.uluru.springboot.discord4j;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.shard.GatewayBootstrap;
import discord4j.gateway.GatewayOptions;
import io.uluru.springboot.discord4j.annotation.EnableDiscord;
import io.uluru.springboot.discord4j.exception.DiscordLoginFailedException;
import io.uluru.springboot.discord4j.exception.MissingTokenConfiguration;

@Configuration
@Import(DiscordProperties.class)
@ConditionalOnBean(annotation = EnableDiscord.class)
public class DiscordAutoConfiguration extends DiscordClientConfig {

	private final static Logger log = LoggerFactory.getLogger(DiscordAutoConfiguration.class);
	
	@Bean
	@ConditionalOnMissingBean
	public DiscordClient discordClient(final DiscordProperties properties) {
		String token = properties.getToken();
		log.info("discordClient, token: {}", token);
		if (StringUtils.isEmpty(token)) {
			throw new MissingTokenConfiguration(String.format("token: %s", token));
		}
		return DiscordClientBuilder.create(token).build();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public EventDispatcher eventDispatcher() {
		return EventDispatcher.builder().build();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public GatewayDiscordClient gatewayDiscordClient(final DiscordClient discordClient, final EventDispatcher eventDispatcher) {
		GatewayBootstrap<GatewayOptions> gateway = discordClient.gateway();
		return gateway
				.setEventDispatcher(eventDispatcher)
				.login()
				.doOnNext(thread -> {
					awaitThread(thread).awaitThread().start();
				})
				.doOnError(error -> {
					new DiscordLoginFailedException(error);
				})
				.block();
	}
	
}
