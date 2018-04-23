package com.javafreelance.montecarlo.controller;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;

@Slf4j
@Controller
public class IndexController {
	
	@RequestMapping({ "", "/", "/index" })
	public String displayIndexPage() {
		log.debug("Displaying index page");
		return "index";
	}

	@GetMapping(path = "/marketdata/feed", produces = TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<SimulatedMarketDataDTO> quotesStream() {
		
		WebClient client = WebClient.create("http://localhost:8080");
		Flux<SimulatedMarketDataDTO> flux = client.get()
	    		.uri("/streamMarketData")
				.accept(APPLICATION_STREAM_JSON)
				.retrieve()
	            .bodyToFlux(SimulatedMarketDataDTO.class);
	    return flux;
	}
}
