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
	
	public boolean isChanceEvent() {
		return makeRandom().nextDouble()<=0.5d;
	}

	public SimulatedMarketDataDTO spinWheel(SimulationConfigurationDTO c, SimulatedMarketDataDTO p, DecimalFormat df) {
		// Prepare a standard normal distribution
		NormalDistribution nd = new NormalDistribution(0d, 1d);
		
		Double bid = p.getBid();
		Double ask = p.getAsk();
		Double last = p.getLast();
		Long bidSize = p.getBidSize();
		Long askSize = p.getAskSize();
		Long lastSize = p.getLastSize();
		Long bidTimeMilliSecs = p.getBidTimeMilliSecs();
		Long askTimeMilliSecs = p.getAskTimeMilliSecs();
		Long lastTimeMilliSecs = p.getLastTimeMilliSecs();
		
		// Calc a simulated time step
		Long timeStepMilliSecs = Math.round(wienerProcess(nd, 0d, c.getTimeStepVolatility(), 1d, c.getAvgTimeStepMilliSecs()));
		
		// Calc the current time in the series and the time step in years
		Long timeMilliSecs = p.getTimeMilliSecs() + timeStepMilliSecs;
		Double timeStepInYears = timeStepMilliSecs * MILLISECS_TO_YEARS;
		
		// Calc the simulated price
		Double simulatedPrice = wienerProcess(nd, c.getRiskFreeReturn(), c.getVolatility(), timeStepInYears, p.getSimulatedPrice());
		
		// Process to determine event type. Possible it can be all three. Must always be at least one.
		double eventRandom = makeRandom().nextDouble();
		boolean bidEvent = isChanceEvent();
		boolean askEvent = isChanceEvent();
		boolean lastEvent = isChanceEvent();
		if(!bidEvent && !askEvent && !lastEvent) {
			bidEvent = eventRandom<=0.33d;
			askEvent = eventRandom>0.33d && eventRandom<=0.66d;
			lastEvent = eventRandom>0.66d;
		}
		
		// Prepare a random to determine a choice
		Integer lastRandom = makeRandom().nextInt(2);
		// Calc the simulated bid-ask spread
		Double simSpread = wienerProcess(nd, 0d, c.getSpreadVolatility(), 1d, c.getTickSize());
		
		if(bidEvent) {
			bidSize = Math.round(wienerProcess(nd, 0d, c.getSizeVolatility(), 1d, c.getAvgBidAskLastSize()));
			bidTimeMilliSecs = timeMilliSecs;
			if(askEvent) { // only move the bid price if there is also an ask event. keep things simple...
				bid = round(simulatedPrice - simSpread, c.getTickSize(), c.getTickScale());
			}
		} 
		
		if(askEvent) {
			askSize = Math.round(wienerProcess(nd, 0d, c.getSizeVolatility(), 1d, c.getAvgBidAskLastSize()));
			askTimeMilliSecs = timeMilliSecs;
			if(bidEvent) { // only move the ask price if there is also an bid event. keep things simple...
				ask = round(Math.max(bid + c.getTickSize(), round(simulatedPrice, c.getTickSize(), c.getTickScale())), c.getTickSize(), c.getTickScale());
			} 
		}
		
		if(lastEvent) {
			lastSize = lastRandom == 0 ? p.getBidSize() : p.getAskSize();
			lastTimeMilliSecs = timeMilliSecs;
			last = lastRandom == 0 ? p.getBid() : p.getAsk();
		}
				
		boolean lastInSeries = false;
		if (timeMilliSecs >= c.getMaxSeriesTimeMilliSecs()) {
			lastInSeries = true;
		}
		// Put it all together
		SimulatedMarketDataDTO smd = new SimulatedMarketDataDTO(timeStepMilliSecs, timeMilliSecs, simulatedPrice, bid, ask, last, bidSize, askSize, lastSize,
				bidTimeMilliSecs, askTimeMilliSecs, lastTimeMilliSecs, df.format(bid), df.format(ask), df.format(last), lastInSeries);
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
