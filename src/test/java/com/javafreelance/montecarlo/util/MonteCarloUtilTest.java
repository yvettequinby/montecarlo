package com.javafreelance.montecarlo.util;

import static org.junit.Assert.*;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Test;

import com.javafreelance.montecarlo.dto.ConfigurationDTO;
import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;

public class MonteCarloUtilTest {

	private ConfigurationDTO makeConfig() {
		Double initialPrice = 69.00d;
		Double tickSize = 0.02d;
		Integer tickScale = 2;
		Long avgTimeStepMilliSecs = 1000L;
		Double volatility = 0.30d;
		Double riskFreeReturn = 0.01d;
		Long avgBidAskLastSize = 2500L;
		Double sizeVolatility = 0.20d;
		Double timeStepVolatility = 0.05;
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
	public void testWienerProcess() {
		ConfigurationDTO config = makeConfig();
		Long seed = 1000L;
		MonteCarloUtil monteCarlo = new MonteCarloUtil(seed);
		Random rand = new Random(seed);
		System.out.println(rand.nextDouble()); // use this in the excel model with other inputs to generate expected values for this test
		NormalDistribution nd = new NormalDistribution(0d, 1d);
		Double timeStepInYears = config.getAvgTimeStepMilliSecs() * MonteCarloUtil.MILLISECS_TO_YEARS;
		Double priceResult = monteCarlo.wienerProcess(nd, config.getRiskFreeReturn(), config.getVolatility(), timeStepInYears,
				config.getInitialPrice());
		priceResult = MonteCarloUtil.round(priceResult, 8);
		Double expectedPriceResult = 69.00204178d; // from Excel model
		assertEquals(expectedPriceResult, priceResult);
		Long timeStepResult = Math.round(monteCarlo.wienerProcess(nd, 0d, config.getTimeStepVolatility(), 1d, config.getAvgTimeStepMilliSecs()));
		Long expectedTimeStepResult = 1027L; // from Excel model
		assertEquals(expectedTimeStepResult, timeStepResult);
		Long sizeResult = Math.round(monteCarlo.wienerProcess(nd, 0d, config.getSizeVolatility(), 1d, config.getAvgBidAskLastSize()));
		Long expectedSizeResult = 2738L; // from Excel model
		assertEquals(expectedSizeResult, sizeResult);
	}

	@Test
	public void testSpinWheel() {
		ConfigurationDTO config = makeConfig();
		SimulatedMarketDataDTO initial = makeInitialSimulatedMarketDataDTO(config);
		Long seed = 1000L;
		MonteCarloUtil monteCarlo = new MonteCarloUtil(seed);
		Random rand = new Random(seed);
		System.out.println(rand.nextDouble()); // use this in the excel model with other inputs to generate expected values for this test
		SimulatedMarketDataDTO result = monteCarlo.spinWheel(config, initial);
		Double priceResult = MonteCarloUtil.round(result.getSimulatedPrice(), 8);
		Double expectedPriceResult = 69.00206916d; // from Excel model
		assertEquals(expectedPriceResult, priceResult); // from Excel model
		Long timeStepResult = result.getTimeStepMilliSecs();
		Long expectedTimeStepResult = 1027L; // from Excel model
		assertEquals(expectedTimeStepResult, timeStepResult);
		Long askSizeResult = result.getAskSize();
		Long expectedSizeResult = 2738L; // from Excel model
		assertEquals(expectedSizeResult, askSizeResult);
	}

	@Test(expected = RuntimeException.class)
	public void testSpinWheelsLongTime() {
		ConfigurationDTO config = makeConfig();
		SimulatedMarketDataDTO initial = makeInitialSimulatedMarketDataDTO(config);
		MonteCarloUtil monteCarlo = new MonteCarloUtil(null);
		SimulatedMarketDataDTO result = monteCarlo.spinWheels(50, config, initial);
		assertNotNull(result);
	}

	@Test
	public void testSpinWheelsShortTime() {
		ConfigurationDTO config = makeConfig();
		SimulatedMarketDataDTO initial = makeInitialSimulatedMarketDataDTO(config);
		MonteCarloUtil monteCarlo = new MonteCarloUtil(null);
		SimulatedMarketDataDTO result = monteCarlo.spinWheels(10, config, initial);
		assertNotNull(result);
	}

	@Test
	public void testRound() {
		Double result = MonteCarloUtil.round(10.49789, 0.02, 2);
		assertEquals(new Double(10.50), result);
		result = MonteCarloUtil.round(10.48189, 0.02, 2);
		assertEquals(new Double(10.48), result);
		result = MonteCarloUtil.round(10.48189, 0.05, 2);
		assertEquals(new Double(10.5), result);
		result = MonteCarloUtil.round(10.38189, 0.5, 1);
		assertEquals(new Double(10.5), result);
		result = MonteCarloUtil.round(10.38189, 1.0, 0);
		assertEquals(new Double(10), result);
		result = MonteCarloUtil.round(10.58189, 1.0, 0);
		assertEquals(new Double(11), result);
		result = MonteCarloUtil.round(12123.58189, 0.25, 2);
		assertEquals(new Double(12123.50), result);
	}
}
