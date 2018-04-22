package com.javafreelance.montecarlo.service;

import reactor.core.publisher.Flux;

import com.javafreelance.montecarlo.dto.ConfigurationDTO;
import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;


public interface MonteCarloService {

	Flux<SimulatedMarketDataDTO> retrieveMonteCarloPublisher(ConfigurationDTO config, SimulatedMarketDataDTO initial);
}
