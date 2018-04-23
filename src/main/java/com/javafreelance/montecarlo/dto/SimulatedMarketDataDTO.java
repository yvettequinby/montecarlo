package com.javafreelance.montecarlo.dto;

import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
		this.endOfSeries = endOfSeries;
	}

	public Long getTimeStepMilliSecs() {
		return timeStepMilliSecs;
	}

	public Long getTimeMilliSecs() {
		return timeMilliSecs;
	}

	public Double getSimulatedPrice() {
		return simulatedPrice;
	}

	public Double getBid() {
		return bid;
	}

	public Double getAsk() {
		return ask;
	}

	public Double getLast() {
		return last;
	}

	public Long getBidSize() {
		return bidSize;
	}

	public Long getAskSize() {
		return askSize;
	}

	public Long getLastSize() {
		return lastSize;
	}

	public boolean isEndOfSeries() {
		return endOfSeries;
	}
}
