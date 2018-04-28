package com.javafreelance.montecarlo.service.impl;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import com.javafreelance.montecarlo.domain.SimulationConfiguration;
import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;
import com.javafreelance.montecarlo.dto.SimulationConfigurationDTO;
import com.javafreelance.montecarlo.mapper.SimulationConfigurationMapper;
import com.javafreelance.montecarlo.model.SimulationConfigurationModel;
import com.javafreelance.montecarlo.repository.SimulationConfigurationRepository;
import com.javafreelance.montecarlo.service.MonteCarloService;
import com.javafreelance.montecarlo.util.MonteCarloUtil;

@Service
@Slf4j
public class MonteCarloServiceImpl implements MonteCarloService {

	private final SimulationConfigurationRepository simulationConfigurationRepository;
	private final SimulationConfigurationMapper simulationConfigurationMapper;

	@Autowired
	public MonteCarloServiceImpl(SimulationConfigurationRepository simulationConfigurationRepository,
			SimulationConfigurationMapper simulationConfigurationMapper) {
		this.simulationConfigurationRepository = simulationConfigurationRepository;
		this.simulationConfigurationMapper = simulationConfigurationMapper;
	}

	@Override
	public Flux<SimulationConfigurationModel> listSimulationConfigurations() {
		return simulationConfigurationRepository.findAll().map(domain -> simulationConfigurationMapper.toModel(domain));
	}

	@Override
	public Mono<SimulationConfigurationModel> getSimulationConfiguration(String id) {
		return simulationConfigurationRepository.findById(id).map(domain -> simulationConfigurationMapper.toModel(domain));
	}
	
	@Override
	public Mono<Void> deleteSimulationConfiguration(String id) {
		return simulationConfigurationRepository.deleteById(id);
	}
	
	@Override
	public Mono<SimulationConfigurationModel> saveSimulationConfiguration(SimulationConfigurationModel model) {
		SimulationConfiguration saveMe = simulationConfigurationMapper.toDomain(model);
		Mono<SimulationConfiguration> mono = simulationConfigurationRepository.save(saveMe);
		return mono.map(domain -> simulationConfigurationMapper.toModel(domain));
	}

	@Override
	public Flux<SimulatedMarketDataDTO> retrieveMonteCarloPublisher(String configId) {
		Mono<SimulationConfigurationDTO> config = simulationConfigurationRepository.findById(configId).map(
				domain -> simulationConfigurationMapper.toDTO(domain));
		Mono<SimulatedMarketDataDTO> initial = makeInitialSimulatedMarketDataDTO(config);
		return retrieveMonteCarloPublisher(config, initial);
	}

	private Flux<SimulatedMarketDataDTO> retrieveMonteCarloPublisher(Mono<SimulationConfigurationDTO> config, Mono<SimulatedMarketDataDTO> initial) {
		final MonteCarloUtil mcu = new MonteCarloUtil(null);
		final ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
		Flux<SimulatedMarketDataDTO> flux = Flux.create(fluxSink -> executeMonteGoGo(producerExecutor, fluxSink, config, initial, mcu));
		return flux;
	}

	private void executeMonteGoGo(ExecutorService producerExecutor, final FluxSink<SimulatedMarketDataDTO> emitter,
			final Mono<SimulationConfigurationDTO> config, final Mono<SimulatedMarketDataDTO> initial, final MonteCarloUtil mcu) {
		producerExecutor.execute(monteGoGo(emitter, config, initial, mcu));
	}

	public Runnable monteGoGo(final FluxSink<SimulatedMarketDataDTO> emitter, final Mono<SimulationConfigurationDTO> config,
			final Mono<SimulatedMarketDataDTO> initial, final MonteCarloUtil mcu) {
		return () -> {
			SimulatedMarketDataDTO p = initial.block();
			SimulationConfigurationDTO c = config.block();
			DecimalFormat df = MonteCarloUtil.buildTickDecimalFormat(c.getTickScale());
			log.debug("Monte Carlo Start!");
			while (p != null && !p.isEndOfSeries()) {
				p = mcu.spinWheel(c, p, df);
				try {
					Thread.sleep(p.getTimeStepMilliSecs());
				} catch (InterruptedException e) {
					log.error("Monte Carlo Sleep Interrupted!", e);
					emitter.error(e);
				}
				emitter.next(p);
			}
			emitter.complete();
			log.debug("Monte Carlo Complete!");
		};
	}

	private Mono<SimulatedMarketDataDTO> makeInitialSimulatedMarketDataDTO(Mono<SimulationConfigurationDTO> configMono) {
		return configMono.map(config -> {
			Long timeStepMilliSecs = config.getAvgTimeStepMilliSecs();
			Long timeMilliSecs = config.getAvgTimeStepMilliSecs();
			Double simulatedPrice = config.getInitialPrice();
			Double bid = config.getInitialPrice() - config.getTickSize();
			Double ask = config.getInitialPrice();
			Double last = config.getInitialPrice();
			Long bidSize = config.getAvgBidAskLastSize();
			Long askSize = config.getAvgBidAskLastSize();
			Long lastSize = config.getAvgBidAskLastSize();
			DecimalFormat df = MonteCarloUtil.buildTickDecimalFormat(config.getTickScale());
			SimulatedMarketDataDTO initial = new SimulatedMarketDataDTO(timeStepMilliSecs, timeMilliSecs, simulatedPrice, bid, ask, last, bidSize,
					askSize, lastSize, timeMilliSecs, timeMilliSecs, timeMilliSecs, df.format(bid), df.format(ask), df.format(last), false);
			return initial;
		});
	}
}
