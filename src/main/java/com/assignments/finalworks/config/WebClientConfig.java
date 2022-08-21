package com.assignments.finalworks.config;

import com.assignments.finalworks.config.properties.BatchProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

	private final BatchProperties batchProperties;

	public WebClientConfig(BatchProperties batchProperties) {
		this.batchProperties = batchProperties;
	}

	@Bean
	public WebClient webClient() {
		ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 50))
			.build();

		return WebClient.builder()
			.clientConnector(
				new ReactorClientHttpConnector(
					HttpClient
						.create()
						.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
						.doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(15))
							.addHandlerLast(new WriteTimeoutHandler(15))
						)
				)
			)
			.exchangeStrategies(exchangeStrategies)
			// 클라이언트의 request나 response를 출력하거나 설정할 수 있다.
			.filter(ExchangeFilterFunction.ofRequestProcessor(
				clientRequest -> Mono.just(clientRequest)
			))
			.filter(ExchangeFilterFunction.ofResponseProcessor(
				clientResponse -> Mono.just(clientResponse)
			))
			.build();
	}

	@Bean
	@Qualifier("port")
	public Long port() {
		return this.batchProperties.getPort();
	}
}
