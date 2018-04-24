package com.javafreelance.montecarlo;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.javafreelance.montecarlo.domain.SimulationConfiguration;
import com.javafreelance.montecarlo.repository.SimulationConfigurationRepository;

@Slf4j
@Component
@Profile("default")
public class DefaultBootstrap implements ApplicationListener<ContextRefreshedEvent> {

	private final SimulationConfigurationRepository simulationConfigurationRepository;

	@Autowired
	public DefaultBootstrap(SimulationConfigurationRepository simulationConfigurationRepository) {
		this.simulationConfigurationRepository = simulationConfigurationRepository;
	}

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.debug("Loading Bootstrap Data");
		loadConfigs();
		log.debug("Loaded Bootstrap Data");
	}
	
	private void loadConfigs() {
		SimulationConfiguration config = new SimulationConfiguration();
		config.setId("000001");
		config.setName("DFLT1");
		config.setInitialPrice(69.00d);
		config.setTickSize(0.01d);
		config.setTickScale(2);
		config.setAvgTimeStepMilliSecs(200L);
		config.setVolatility(0.35d);
		config.setRiskFreeReturn(0.05d);
		config.setAvgBidAskLastSize(2500L);
		config.setSizeVolatility(0.3d);
		config.setTimeStepVolatility(0.15d);
		config.setSpreadVolatility(0.2d);
		config.setMaxSeriesTimeMilliSecs(1000L * 30);
		simulationConfigurationRepository.save(config).block();
		
		config = new SimulationConfiguration();
		config.setId("000002");
		config.setName("FDAX");
		config.setInitialPrice(10500.00d);
		config.setTickSize(0.25d);
		config.setTickScale(2);
		config.setAvgTimeStepMilliSecs(200L);
		config.setVolatility(0.35d);
		config.setRiskFreeReturn(0.05d);
		config.setAvgBidAskLastSize(5L);
		config.setSizeVolatility(0.3d);
		config.setTimeStepVolatility(0.15d);
		config.setSpreadVolatility(0.2d);
		config.setMaxSeriesTimeMilliSecs(1000L * 60);
		simulationConfigurationRepository.save(config).block();
	}
	

	
}
