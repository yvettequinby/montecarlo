package com.javafreelance.montecarlo.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import com.javafreelance.montecarlo.dto.ConfigurationDTO;
import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;
import com.javafreelance.montecarlo.service.MonteCarloService;
import com.javafreelance.montecarlo.util.MonteCarloUtil;

@Service
@Slf4j
public class MonteCarloServiceImpl implements MonteCarloService {

	@Override
	public Flux<SimulatedMarketDataDTO> retrieveMonteCarloPublisher(final ConfigurationDTO config, final SimulatedMarketDataDTO initial) {
		final MonteCarloUtil mcu = new MonteCarloUtil(null);
		final ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
		Flux<SimulatedMarketDataDTO> flux = Flux.create(fluxSink -> executeMonteGoGo(producerExecutor, fluxSink, config, initial, mcu));
		return flux;
	}

	private void executeMonteGoGo(ExecutorService producerExecutor, final FluxSink<SimulatedMarketDataDTO> emitter, final ConfigurationDTO config,
			final SimulatedMarketDataDTO initial, final MonteCarloUtil mcu) {
		producerExecutor.execute(monteGoGo(emitter, config, initial, mcu));
	}

	public Runnable monteGoGo(final FluxSink<SimulatedMarketDataDTO> emitter, final ConfigurationDTO config, final SimulatedMarketDataDTO initial,
			final MonteCarloUtil mcu) {
		return () -> {
			SimulatedMarketDataDTO p = initial;
			while (p != null && !p.isEndOfSeries()) {
				p = mcu.spinWheel(config, p);
				try {
					Thread.sleep(p.getTimeStepMilliSecs());
				} catch (InterruptedException e) {
					log.error("Monte Carlo Sleep Interrupted!", e);
					emitter.error(e);
				}
				emitter.next(p);
			}
			emitter.complete();
		};
	}
}
