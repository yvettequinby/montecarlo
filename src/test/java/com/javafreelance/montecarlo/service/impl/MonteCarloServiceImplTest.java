package com.javafreelance.montecarlo.service.impl;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import reactor.core.publisher.Flux;

import com.javafreelance.montecarlo.dto.ConfigurationDTO;
import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;

@Slf4j
public class MonteCarloServiceImplTest {

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

	@Test
	public void testSubscribe() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		ConfigurationDTO config = makeConfig();
		SimulatedMarketDataDTO initial = makeInitialSimulatedMarketDataDTO(config);
		MonteCarloServiceImpl monteCarloService = new MonteCarloServiceImpl();
		Flux<SimulatedMarketDataDTO> fluxPublisher = monteCarloService.retrieveMonteCarloPublisher(config, initial);
		fluxPublisher.subscribe(smd -> {
			log.debug(smd.toString());
		}, error -> {
			log.error("Error in flux publisher! ", error);
			latch.countDown();
		}, () -> {
			System.out.println("Flux publisher complete!");
			latch.countDown();
		});
		latch.await();
		assertNotNull("Flux capacitors overloaded!");
	}
}
