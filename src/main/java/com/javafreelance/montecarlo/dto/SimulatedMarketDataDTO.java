package com.javafreelance.montecarlo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class SimulatedMarketDataDTO {

	private final Long timeStepMilliSecs;
	private final Long timeMilliSecs;
	private final Double simulatedPrice;
	private final Double bid;
	private final Double ask;
	private final Double last;
	private final Long bidSize;
	private final Long askSize;
	private final Long lastSize;
	private final boolean endOfSeries;
}
