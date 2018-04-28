package com.javafreelance.montecarlo.dto;

import lombok.Getter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
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
	private final Long bidTimeMilliSecs;
	private final Long askTimeMilliSecs;
	private final Long lastTimeMilliSecs;
	private final String bidString;
	private final String askString;
	private final String lastString;
	private final boolean endOfSeries;

	@JsonCreator
	public SimulatedMarketDataDTO(@JsonProperty("timeStepMilliSecs")Long timeStepMilliSecs, 
			@JsonProperty("timeMilliSecs")Long timeMilliSecs, 
			@JsonProperty("simulatedPrice")Double simulatedPrice, 
			@JsonProperty("bid")Double bid, 
			@JsonProperty("ask")Double ask, 
			@JsonProperty("last")Double last,
			@JsonProperty("bidSize")Long bidSize, 
			@JsonProperty("askSize")Long askSize, 
			@JsonProperty("lastSize")Long lastSize, 
			@JsonProperty("bidTimeMilliSecs")Long bidTimeMilliSecs, 
			@JsonProperty("askTimeMilliSecs")Long askTimeMilliSecs, 
			@JsonProperty("lastTimeMilliSecs")Long lastTimeMilliSecs, 
			@JsonProperty("bidString")String bidString, 
			@JsonProperty("askString")String askString, 
			@JsonProperty("lastString")String lastString, 
			@JsonProperty("endOfSeries")boolean endOfSeries) {
		super();
		this.timeStepMilliSecs = timeStepMilliSecs;
		this.timeMilliSecs = timeMilliSecs;
		this.simulatedPrice = simulatedPrice;
		this.bid = bid;
		this.ask = ask;
		this.last = last;
		this.bidSize = bidSize;
		this.askSize = askSize;
		this.lastSize = lastSize;
		this.bidTimeMilliSecs = bidTimeMilliSecs;
		this.askTimeMilliSecs = askTimeMilliSecs;
		this.lastTimeMilliSecs = lastTimeMilliSecs;
		this.bidString = bidString;
		this.askString = askString;
		this.lastString = lastString;
		this.endOfSeries = endOfSeries;
	}
	
	

}
