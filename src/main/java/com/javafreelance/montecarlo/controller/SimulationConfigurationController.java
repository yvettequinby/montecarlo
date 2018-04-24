package com.javafreelance.montecarlo.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import reactor.core.publisher.Flux;

import com.javafreelance.montecarlo.model.SimulationConfigurationModel;
import com.javafreelance.montecarlo.service.MonteCarloService;

@Slf4j
@Controller
public class SimulationConfigurationController {
	
	private final MonteCarloService monteCarloService;
	
	public SimulationConfigurationController(MonteCarloService monteCarloService) {
		this.monteCarloService = monteCarloService;
	}
	
	@RequestMapping({ "", "/", "/index", "/configuration", "/configuration/list"})
	public String displayIndexPage(Model model) {
		log.debug("Displaying configuration list page");
		Flux<SimulationConfigurationModel> configs = monteCarloService.listSimulationConfigurations();
		model.addAttribute("configs", configs);
		return "index";
	}
}
