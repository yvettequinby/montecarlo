package com.javafreelance.montecarlo.controller;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;
import com.javafreelance.montecarlo.service.MonteCarloService;

@Component
public class MonteCarloHandler {

	private final MonteCarloService monteCarloService;

	@Autowired
	public MonteCarloHandler(MonteCarloService monteCarloService) {
		super();
		this.monteCarloService = monteCarloService;
	}

	public Mono<ServerResponse> streamMarketData(ServerRequest request) {
		String configId = request.pathVariable("configId");
		final Flux<SimulatedMarketDataDTO> mdFlux = monteCarloService.retrieveMonteCarloPublisher(configId);
		return ok().contentType(APPLICATION_STREAM_JSON).body(mdFlux, SimulatedMarketDataDTO.class);
	}
}
