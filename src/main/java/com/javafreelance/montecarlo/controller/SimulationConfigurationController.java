package com.javafreelance.montecarlo.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Flux;

import com.javafreelance.montecarlo.model.SimulationConfigurationModel;
import com.javafreelance.montecarlo.service.MonteCarloService;

@Slf4j
@Controller
public class SimulationConfigurationController {

	private final MonteCarloService monteCarloService;
	private WebDataBinder webDataBinder; // hacky: webflux has issues with binding and validation, so we have to do some manual stuff to validate

	@Autowired
	public SimulationConfigurationController(MonteCarloService monteCarloService) {
		this.monteCarloService = monteCarloService;
	}

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		this.webDataBinder = webDataBinder;
	}

	@RequestMapping({ "", "/", "/index", "/configuration", "/configuration/list" })
	public String listConfigurations(@RequestParam(required = false) String msg, Model model) {
		log.debug("Displaying configuration list page");
		Flux<SimulationConfigurationModel> configs = monteCarloService.listSimulationConfigurations();
		model.addAttribute("configs", configs);
		if (msg != null) {
			model.addAttribute("msg", msg);
		}
		return "index";
	}

	@GetMapping
	@RequestMapping("/configuration/{id}/view")
	public String viewConfiguration(@PathVariable String id, @RequestParam(required = false) String msg, Model model) {
		log.debug("Displaying configuration details page");
		SimulationConfigurationModel configModel = monteCarloService.getSimulationConfiguration(id).block();
		model.addAttribute("config", configModel);
		if (msg != null) {
			model.addAttribute("msg", msg);
		}
		return "configuration";
	}
	
	@GetMapping
	@RequestMapping("/configuration/create")
	public String createConfiguration(Model model) {
		log.debug("Displaying configuration create page");
		SimulationConfigurationModel configModel = new SimulationConfigurationModel();
		configModel.setAvgBidAskLastSize("1000");
		configModel.setInitialPrice("72.03");
		configModel.setAvgTimeStepMilliSecs("1000");
		configModel.setMaxSeriesTimeMilliSecs("60000");
		configModel.setName("ABC VX");
		configModel.setRiskFreeReturn("0.01");
		configModel.setSizeVolatility("0.10");
		configModel.setSpreadVolatility("0.50");
		configModel.setTickScale("2");
		configModel.setTickSize("0.01");
		configModel.setTimeStepVolatility("0.20");
		configModel.setVolatility("0.30");
		model.addAttribute("config", configModel);
		return "configuration";
	}

	@GetMapping
	@RequestMapping("/configuration/{id}/delete")
	public String deleteConfiguration(@PathVariable String id, Model model) {
		log.debug("Delete configuration: " + id);
		monteCarloService.deleteSimulationConfiguration(id).block();
		return "redirect:/configuration/list?msg=" + urlEncode("Record deleted.");
	}

	@PostMapping("configuration/save")
	public String saveOrUpdate(@ModelAttribute("config") SimulationConfigurationModel config) {
		log.debug("Saving configuration");
		webDataBinder.validate();
		BindingResult bindingResult = webDataBinder.getBindingResult();
		if (bindingResult.hasErrors()) {
			return "configuration";
		}
		SimulationConfigurationModel savedConfig = monteCarloService.saveSimulationConfiguration(config).block();
		return "redirect:/configuration/" + savedConfig.getId() + "/view?msg=" + urlEncode("Record saved.");
	}

	private String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
