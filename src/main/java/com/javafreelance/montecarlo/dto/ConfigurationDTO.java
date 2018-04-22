package com.javafreelance.montecarlo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfigurationDTO {

	private final Double initialPrice;
	private final Double tickSize;
	private final Integer tickScale; // for help managing stupid doubles
	private final Long avgTimeStepMilliSecs;
	private final Double volatility; // should be provided as an annualized figure
	private final Double riskFreeReturn; // should be provided as an annualized figure
	private final Long avgBidAskLastSize;
	private final Double sizeVolatility;
	private final Double timeStepVolatility;
	private final Double spreadVolatility;
	private final Long maxSeriesTimeMilliSecs;
}
