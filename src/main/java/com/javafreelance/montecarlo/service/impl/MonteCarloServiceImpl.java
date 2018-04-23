package com.javafreelance.montecarlo.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import com.javafreelance.montecarlo.dto.ConfigurationDTO;
import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;
import com.javafreelance.montecarlo.service.MonteCarloService;
import com.javafreelance.montecarlo.util.MonteCarloUtil;

@Service
@Slf4j
public class MonteCarloServiceImpl implements MonteCarloService {

	@Override
	public Flux<SimulatedMarketDataDTO> retrieveDefaultMonteCarloPublisher() {
		final ConfigurationDTO config = makeConfig();
		final SimulatedMarketDataDTO initial = makeInitialSimulatedMarketDataDTO(config);
		return retrieveMonteCarloPublisher(config, initial);
	}

	@Override
	public Flux<SimulatedMarketDataDTO> retrieveMonteCarloPublisher(final ConfigurationDTO config, final SimulatedMarketDataDTO initial) {
		final MonteCarloUtil mcu = new MonteCarloUtil(null);
		final ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
		Flux<SimulatedMarketDataDTO> flux = Flux.create(fluxSink -> executeMonteGoGo(producerExecutor, fluxSink, config, initial, mcu));
		return flux;
	}

	private void executeMonteGoGo(ExecutorService producerExecutor, final FluxSink<SimulatedMarketDataDTO> emitter, final ConfigurationDTO config,
			final SimulatedMarketDataDTO initial, final MonteCarloUtil mcu) {
		producerExecutor.execute(monteGoGo(emitter, config, initial, mcu));
	}

	public Runnable monteGoGo(final FluxSink<SimulatedMarketDataDTO> emitter, final ConfigurationDTO config, final SimulatedMarketDataDTO initial,
			final MonteCarloUtil mcu) {
		return () -> {
			SimulatedMarketDataDTO p = initial;
			log.debug("Monte Carlo Start!");
			while (p != null && !p.isEndOfSeries()) {
				p = mcu.spinWheel(config, p);
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

	private ConfigurationDTO makeConfig() {
		Double initialPrice = 69.00d;
		Double tickSize = 0.01d;
		Integer tickScale = 2;
		Long avgTimeStepMilliSecs = 200L;
		Double volatility = 0.35d;
		Double riskFreeReturn = 0.01d;
		Long avgBidAskLastSize = 2500L;
		Double sizeVolatility = 0.20d;
		Double timeStepVolatility = 0.15;
		Double spreadVolatility = 0.2;
		Long maxSeriesTimeMilliSecs = 1000L * 30; // 30 seconds
		ConfigurationDTO config = new ConfigurationDTO(initialPrice, tickSize, tickScale, avgTimeStepMilliSecs, volatility, riskFreeReturn,
				avgBidAskLastSize, sizeVolatility, timeStepVolatility, spreadVolatility, maxSeriesTimeMilliSecs);
		return config;
	}

	private SimulatedMarketDataDTO makeInitialSimulatedMarketDataDTO(ConfigurationDTO config) {
		Long timeStepMilliSecs = config.getAvgTimeStepMilliSecs();
		Long timeMilliSecs = config.getAvgTimeStepMilliSecs();
		Double simulatedPrice = config.getInitialPrice();
		Double bid = config.getInitialPrice() - config.getTickSize();
		Double ask = config.getInitialPrice();
		Double last = config.getInitialPrice();
		Long bidSize = config.getAvgBidAskLastSize();
		Long askSize = config.getAvgBidAskLastSize();
		Long lastSize = config.getAvgBidAskLastSize();
		SimulatedMarketDataDTO initial = new SimulatedMarketDataDTO(timeStepMilliSecs, timeMilliSecs, simulatedPrice, bid, ask, last, bidSize,
				askSize, lastSize, false);
		return initial;
	}
}
