package com.javafreelance.montecarlo.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;
import com.javafreelance.montecarlo.model.SimulationConfigurationModel;

public interface MonteCarloService {

	Flux<SimulatedMarketDataDTO> retrieveMonteCarloPublisher(String configId);

	Flux<SimulationConfigurationModel> listSimulationConfigurations();

	Mono<SimulationConfigurationModel> getSimulationConfiguration(String id);

	Mono<SimulationConfigurationModel> saveSimulationConfiguration(SimulationConfigurationModel model);

	Mono<Void> deleteSimulationConfiguration(String id);
}
