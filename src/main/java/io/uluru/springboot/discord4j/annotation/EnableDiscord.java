package io.uluru.springboot.discord4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import io.uluru.springboot.discord4j.DiscordAutoConfiguration;
import io.uluru.springboot.discord4j.processor.DiscordBeanPostProcessor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
	DiscordAutoConfiguration.class,
	DiscordBeanPostProcessor.class
})
public @interface EnableDiscord {

}
