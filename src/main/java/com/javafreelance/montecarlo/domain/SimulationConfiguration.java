package com.javafreelance.montecarlo.domain;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class SimulationConfiguration {
	
	@Id
	private String id;
	private String name;
	private Double initialPrice;
	private Double tickSize;
	private Integer tickScale;
	private Long avgTimeStepMilliSecs;
	private Double volatility;
	private Double riskFreeReturn;
	private Long avgBidAskLastSize;
	private Double sizeVolatility;
	private Double timeStepVolatility;
	private Double spreadVolatility;
	private Long maxSeriesTimeMilliSecs;
	
}
