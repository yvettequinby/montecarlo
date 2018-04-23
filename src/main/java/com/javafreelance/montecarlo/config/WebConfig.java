package com.javafreelance.montecarlo.config;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.javafreelance.montecarlo.controller.MonteCarloHandler;

@Configuration
public class WebConfig {

	@Bean
	public RouterFunction<ServerResponse> route(MonteCarloHandler monteCarloHandler) {
		return RouterFunctions.route(GET("/streamMarketData").and(accept(APPLICATION_STREAM_JSON)), monteCarloHandler::streamMarketData);
	}
}
