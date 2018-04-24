package com.javafreelance.montecarlo.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.math3.distribution.NormalDistribution;

import com.javafreelance.montecarlo.dto.SimulationConfigurationDTO;
import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;

@AllArgsConstructor
@Slf4j
public class MonteCarloUtil {

	private final Long seed;
	public static final Double MILLISECS_TO_YEARS = new Double("0.0000000000317097919837646");

	public SimulatedMarketDataDTO spinWheels(int spins, SimulationConfigurationDTO config, SimulatedMarketDataDTO initial) {
		SimulatedMarketDataDTO result = initial;
		DecimalFormat df = buildTickDecimalFormat(config.getTickScale());
		for (int i = 0; i < spins; i++) {
			if (result.isEndOfSeries()) {
				throw new RuntimeException("End of series reached before spins complete!");
			}
			result = spinWheel(config, result, df);
			log.debug(result.toString());
		}
		return result;
	}

	public SimulatedMarketDataDTO spinWheel(SimulationConfigurationDTO c, SimulatedMarketDataDTO p, DecimalFormat df) {
		// Prepare a standard normal distribution
		NormalDistribution nd = new NormalDistribution(0d, 1d);
		// Calc a simulated time step
		Long simTimeStep = Math.round(wienerProcess(nd, 0d, c.getTimeStepVolatility(), 1d, c.getAvgTimeStepMilliSecs()));
		// Calc the current time in the series and the time step in years
		Long time = p.getTimeMilliSecs() + simTimeStep;
		Double timeStepInYears = simTimeStep * MILLISECS_TO_YEARS;
		// Calc the simulated price
		Double simPrice = wienerProcess(nd, c.getRiskFreeReturn(), c.getVolatility(), timeStepInYears, p.getSimulatedPrice());
		// Prepare a random to determine a choice
		Integer lastRandom = makeRandom().nextInt(4);
		// Calc the simulated bid-ask spread
		Double simSpread = wienerProcess(nd, 0d, c.getSpreadVolatility(), 1d, c.getTickSize());
		// Sizes
		Long simBidSize = Math.round(wienerProcess(nd, 0d, c.getSizeVolatility(), 1d, c.getAvgBidAskLastSize()));
		Long simAskSize = Math.round(wienerProcess(nd, 0d, c.getSizeVolatility(), 1d, c.getAvgBidAskLastSize()));
		Long simLastSize = lastRandom == 0 ? p.getBidSize() : lastRandom == 1 ? p.getAskSize() : lastRandom == 2 ? simBidSize : simAskSize;
		// Down to business - bid, ask and last
		Double bid = round(simPrice - simSpread, c.getTickSize(), c.getTickScale());
		Double ask = round(Math.max(bid + c.getTickSize(), round(simPrice, c.getTickSize(), c.getTickScale())), c.getTickSize(), c.getTickScale());
		Double last = lastRandom == 0 ? p.getBid() : lastRandom == 1 ? p.getAsk() : lastRandom == 2 ? bid : ask;
		boolean lastInSeries = false;
		if (time >= c.getMaxSeriesTimeMilliSecs()) {
			lastInSeries = true;
		}
		// Put it all together
		SimulatedMarketDataDTO smd = new SimulatedMarketDataDTO(simTimeStep, time, simPrice, bid, ask, last, simBidSize, simAskSize, simLastSize,
				df.format(bid), df.format(ask), df.format(last), lastInSeries);
		return smd;
	}
	
	public static DecimalFormat buildTickDecimalFormat(Integer scale) {
		DecimalFormat df = new DecimalFormat();
		String pattern = "0.00";
		if(scale>2) {
			for(int i=2; i<=scale; i++) {
				pattern = pattern + "0";
			}
		}
		df.applyPattern(pattern);
		return df;
	}

	public Double wienerProcess(NormalDistribution nd, double r, double sigma, double t, double s) {
		// log.debug("WEINER PROCESS. r: " + r + ", sigma: " + sigma + ", t: " + t + ", s: " + s);
		Double sqrtT = Math.sqrt(t);
		// log.debug("sqrtT = Math.sqrt(t): " + sqrtT);
		Double rand = makeRandom().nextDouble();
		// log.debug("rand = makeRandom().nextDouble(): " + rand);
		Double inverseNdCum = nd.inverseCumulativeProbability(rand);
		// log.debug("inverseNdCum = nd.inverseCumulativeProbability(rand): " + inverseNdCum);
		Double powSigma = Math.pow(sigma, 2d);
		// log.debug("powSigma = Math.pow(sigma, 2d): " + powSigma);
		Double exp = (r - powSigma / 2d) * t + inverseNdCum * sigma * sqrtT;
		// log.debug("exp = (r - powSigma / 2d) * t + inverseNdCum * sigma * sqrtT: " + exp);
		Double weiner = s * Math.exp(exp);
		// log.debug("weiner = s * Math.exp(exp): " + weiner);
		return weiner;
	}

	private Random makeRandom() {
		if (seed == null) {
			return new Random();
		} else {
			return new Random(seed);
		}
	}

	public static Double round(double roundMe, double to, Integer scale) {
		Double d = Math.round(roundMe / to) * to;
		d = BigDecimal.valueOf(d).setScale(scale, RoundingMode.HALF_UP).doubleValue();
		return d;
	}

	public static Double round(double roundMe, Integer scale) {
		Double d = BigDecimal.valueOf(roundMe).setScale(scale, RoundingMode.HALF_UP).doubleValue();
		return d;
	}
}
