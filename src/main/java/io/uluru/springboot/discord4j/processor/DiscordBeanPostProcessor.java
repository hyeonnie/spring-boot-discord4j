package io.uluru.springboot.discord4j.processor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.reactivestreams.Publisher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import io.uluru.springboot.discord4j.annotation.DiscordErrorHandler;
import io.uluru.springboot.discord4j.annotation.DiscordEventListener;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnBean(GatewayDiscordClient.class)
public class DiscordBeanPostProcessor implements BeanPostProcessor {

	private final EventDispatcher eventDispatcher;

	@Autowired
	public DiscordBeanPostProcessor(final EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		ReflectionUtils.doWithMethods(bean.getClass(), method -> {
			if (method.isAnnotationPresent(DiscordEventListener.class)) {
				discordEventListener(method, bean);
			}
		});
		return bean;
	}

	@SuppressWarnings("unchecked")
	private void discordEventListener(Method method, Object bean) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length > 0) {
			Class<?> parameterType = parameterTypes[0];
			if (Event.class.isAssignableFrom(parameterType)) {
				event(method, bean, (Class<? extends Event>) parameterType);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void event(Method method, Object bean, Class<? extends Event> clazz) {
		eventDispatcher.on(clazz)
		.flatMap(event -> {
			Optional<Object> object = Optional.ofNullable(ReflectionUtils.invokeMethod(method, bean, event));
			return object
					.filter(publisher -> publisher instanceof Publisher)
					.map(publisher -> (Publisher) publisher)
					.orElse(Mono.empty());
		})
		.doOnError(error -> {
			error(bean, error);
			event(method, bean, clazz);
		})
		.subscribe();
	}
	
	private void error(Object bean, Object error) {
		Optional<Method> errorHandler = Arrays.stream(bean.getClass().getDeclaredMethods())
				.filter(method -> method.isAnnotationPresent(DiscordErrorHandler.class))
                .findFirst();
        errorHandler.ifPresent(method -> ReflectionUtils.invokeMethod(method, bean, error));
	}

}
