package com.javafreelance.montecarlo.model;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimulationConfigurationModel {

	private String id;
	@NotBlank
	@Size(min = 2, max = 7)
	private String name;
	@NotBlank
	@Size(min = 1, max = 13)
	@Digits(integer = 7, fraction = 5)
	private String initialPrice;
	@NotBlank
	@Size(min = 1, max = 5)
	@Digits(integer = 2, fraction = 4)
	private String tickSize;
	@NotBlank
	@Size(min = 1, max = 1)
	@Digits(integer = 1, fraction = 0)
	private String tickScale;
	@NotBlank
	@Size(min = 1, max = 6)
	@Digits(integer = 6, fraction = 0)
	private String avgTimeStepMilliSecs;
	@NotBlank
	@Size(min = 1, max = 4)
	@Digits(integer = 1, fraction = 3)
	private String volatility;
	@NotBlank
	@Size(min = 1, max = 6)
	@Digits(integer = 1, fraction = 5)
	private String riskFreeReturn;
	@NotBlank
	@Size(min = 1, max = 10)
	@Digits(integer = 10, fraction = 0)
	private String avgBidAskLastSize;
	@NotBlank
	@Size(min = 1, max = 4)
	@Digits(integer = 1, fraction = 3)
	private String sizeVolatility;
	@NotBlank
	@Size(min = 1, max = 4)
	@Digits(integer = 1, fraction = 3)
	private String timeStepVolatility;
	@NotBlank
	@Size(min = 1, max = 4)
	@Digits(integer = 1, fraction = 3)
	private String spreadVolatility;
	@NotBlank
	@Size(min = 1, max = 10)
	@Digits(integer = 10, fraction = 0)
	private String maxSeriesTimeMilliSecs;

	/*@AssertTrue
	public boolean isValidInitialPrice() {
		return isValidPositiveDecimal(initialPrice);
	}

	@AssertTrue
	public boolean isValidTickSize() {
		return isValidPositiveDecimal(tickSize);
	}

	@AssertTrue
	public boolean isValidTickScale() {
		return isValidPositiveInteger(tickScale);
	}

	@AssertTrue
	public boolean isValidVolatility() {
		return isValidPositiveDecimal(volatility);
	}

	@AssertTrue
	public boolean isValidRiskFreeReturn() {
		return isValidPositiveDecimal(riskFreeReturn);
	}

	@AssertTrue
	public boolean isValidavgBidAskLastSize() {
		return isValidPositiveInteger(avgBidAskLastSize);
	}

	@AssertTrue
	public boolean isValidSizeVolatility() {
		return isValidPositiveDecimal(sizeVolatility);
	}

	@AssertTrue
	public boolean isValidTimeStepVolatility() {
		return isValidPositiveDecimal(timeStepVolatility);
	}

	@AssertTrue
	public boolean isValidSpreadVolatility() {
		return isValidPositiveDecimal(spreadVolatility);
	}

	@AssertTrue
	public boolean isValidMaxSeriesTimeMilliSecs() {
		return isValidPositiveDecimal(maxSeriesTimeMilliSecs);
	}

	private boolean isValidPositiveDecimal(String s) {
		boolean result = false;
		BigDecimal bd = makeBigDecimal(s);
		if (bd != null && bd.compareTo(new BigDecimal("0")) > 0) {
			result = true;
		}
		return result;
	}

	private boolean isValidPositiveInteger(String s) {
		boolean result = false;
		Integer i = makeInteger(s);
		if (i != null && i > 0) {
			result = true;
		}
		return result;
	}

	private boolean isValidPositiveLong(String s) {
		boolean result = false;
		Long i = makeLong(s);
		if (i != null && i > 0L) {
			result = true;
		}
		return result;
	}

	private BigDecimal makeBigDecimal(String bd) {
		BigDecimal result = null;
		try {
			result = new BigDecimal(bd);
		} catch (Exception e) {
			// shhh
		}
		return result;
	}

	private Integer makeInteger(String s) {
		Integer result = null;
		try {
			result = new Integer(s);
		} catch (Exception e) {
			// shhh
		}
		return result;
	}

	private Long makeLong(String s) {
		Long result = null;
		try {
			result = new Long(s);
		} catch (Exception e) {
			// shhh
		}
		return result;
	}*/
}
