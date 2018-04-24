package com.javafreelance.montecarlo.mapper;

import lombok.Synchronized;

import org.springframework.stereotype.Component;

import com.javafreelance.montecarlo.domain.SimulationConfiguration;
import com.javafreelance.montecarlo.dto.SimulationConfigurationDTO;
import com.javafreelance.montecarlo.model.SimulationConfigurationModel;

@Component
public class SimulationConfigurationMapper {

	@Synchronized
	public SimulationConfigurationModel toModel(SimulationConfiguration domain) {
		SimulationConfigurationModel model = null;
		if (domain != null) {
			model = new SimulationConfigurationModel();
			model.setAvgBidAskLastSize(domain.getAvgBidAskLastSize().toString());
			model.setAvgTimeStepMilliSecs(domain.getAvgTimeStepMilliSecs().toString());
			model.setId(domain.getId());
			model.setInitialPrice(domain.getInitialPrice().toString());
			model.setMaxSeriesTimeMilliSecs(domain.getMaxSeriesTimeMilliSecs().toString());
			model.setName(domain.getName());
			model.setRiskFreeReturn(domain.getRiskFreeReturn().toString());
			model.setSizeVolatility(domain.getSizeVolatility().toString());
			model.setSpreadVolatility(domain.getSpreadVolatility().toString());
			model.setTickScale(domain.getTickScale().toString());
			model.setTickSize(domain.getTickSize().toString());
			model.setTimeStepVolatility(domain.getTimeStepVolatility().toString());
			model.setVolatility(domain.getVolatility().toString());
		}
		return model;
	}
	
	@Synchronized
	public SimulationConfigurationDTO toDTO(SimulationConfiguration domain) {
		SimulationConfigurationDTO dto = null;
		if (domain != null) {
			dto = new SimulationConfigurationDTO(
					domain.getId(),
					domain.getName(),
					domain.getInitialPrice(),
					domain.getTickSize(),
					domain.getTickScale(),
					domain.getAvgTimeStepMilliSecs(),
					domain.getVolatility(),
					domain.getRiskFreeReturn(),
					domain.getAvgBidAskLastSize(),
					domain.getSizeVolatility(),
					domain.getTimeStepVolatility(),
					domain.getSpreadVolatility(),
					domain.getMaxSeriesTimeMilliSecs());
		}
		return dto;
	}
}
