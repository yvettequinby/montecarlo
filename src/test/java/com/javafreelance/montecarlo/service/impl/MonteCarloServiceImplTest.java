package com.javafreelance.montecarlo.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;

import org.junit.Before;
import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;
import com.javafreelance.montecarlo.dto.SimulationConfigurationDTO;
import com.javafreelance.montecarlo.util.MonteCarloUtil;

@Slf4j
public class MonteCarloServiceImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		CountDownLatch latch = new CountDownLatch(1);
		MonteCarloServiceImpl service = new MonteCarloServiceImpl(null, null);
		SimulationConfigurationDTO config = makeConfig();
		Mono<SimulationConfigurationDTO> configMono = Mono.just(config);
		Mono<SimulatedMarketDataDTO> initialMono = Mono.just(makeInitialSimulatedMarketDataDTO(config));
		Flux<SimulatedMarketDataDTO> flux = service.retrieveMonteCarloPublisher(configMono, initialMono);
		final AtomicBoolean gotData = new AtomicBoolean(false);
		flux.subscribe(
				m -> {
					log.debug(m.toString());
					gotData.set(true);
				}, 
				e -> {
					log.error("Error: " + e);
					latch.countDown();
					fail("Unexpected Error");
				}, 
				()->{
					log.debug("Complete!");
					latch.countDown();
				});
		try {
			latch.await(7000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			fail("Unexpected Error");
		}
		assertTrue(gotData.get());
	}
	
	private SimulationConfigurationDTO makeConfig() {
		String id = "000001";
		String name = "DFLT";
		Double initialPrice = 69.00d;
		Double tickSize = 0.02d;
		Integer tickScale = 2;
		Long avgTimeStepMilliSecs = 500L;
		Double volatility = 0.30d;
		Double riskFreeReturn = 0.01d;
		Long avgBidAskLastSize = 2500L;
		Double sizeVolatility = 0.20d;
		Double timeStepVolatility = 0.05;
		Double spreadVolatility = 0.2;
		Long maxSeriesTimeMilliSecs = 1000L * 5; // 5 seconds
		SimulationConfigurationDTO config = new SimulationConfigurationDTO(id, name, initialPrice, tickSize, tickScale, avgTimeStepMilliSecs, volatility, riskFreeReturn,
				avgBidAskLastSize, sizeVolatility, timeStepVolatility, spreadVolatility, maxSeriesTimeMilliSecs);
		return config;
	}

	private SimulatedMarketDataDTO makeInitialSimulatedMarketDataDTO(SimulationConfigurationDTO config) {
		DecimalFormat df = MonteCarloUtil.buildTickDecimalFormat(config.getTickScale());
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
				askSize, lastSize, timeMilliSecs, timeMilliSecs, timeMilliSecs, df.format(bid), df.format(ask), df.format(last), false);
		return initial;
	}
}
