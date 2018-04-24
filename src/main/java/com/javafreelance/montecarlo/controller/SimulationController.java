package com.javafreelance.montecarlo.controller;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;
import com.javafreelance.montecarlo.model.SimulationConfigurationModel;
import com.javafreelance.montecarlo.service.MonteCarloService;


@Slf4j
@Controller
public class SimulationController {
	
	private final MonteCarloService monteCarloService;
	
	public SimulationController(MonteCarloService monteCarloService) {
		this.monteCarloService = monteCarloService;
	}
	
	@GetMapping
	@RequestMapping("/simulation/{configurationId}/view")
	public String displaySimulationPage(@PathVariable String configurationId, Model model) {
		log.debug("Displaying simulation page");
		Mono<SimulationConfigurationModel> config = monteCarloService.getSimulationConfiguration(configurationId);
		model.addAttribute("config", config);
		return "simulation";
	}
	
	@GetMapping(path = "/simulation/{configurationId}/stream", produces = TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<SimulatedMarketDataDTO> simulationStream(@PathVariable String configurationId) {
		
		WebClient client = WebClient.create("http://localhost:8080");
		Flux<SimulatedMarketDataDTO> flux = client.get()
	    		.uri("/publishing/sim/" + configurationId)
				.accept(APPLICATION_STREAM_JSON)
				.retrieve()
	            .bodyToFlux(SimulatedMarketDataDTO.class);
	    return flux;
	}
}
