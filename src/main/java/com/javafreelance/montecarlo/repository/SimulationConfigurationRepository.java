package com.javafreelance.montecarlo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.javafreelance.montecarlo.domain.SimulationConfiguration;


public interface SimulationConfigurationRepository extends ReactiveMongoRepository<SimulationConfiguration, String> {
}
