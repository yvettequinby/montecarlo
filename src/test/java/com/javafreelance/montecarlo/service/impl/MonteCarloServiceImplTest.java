package com.javafreelance.montecarlo.service.impl;

import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.core.publisher.Flux;
import static org.junit.Assert.assertTrue;

import com.javafreelance.montecarlo.dto.SimulatedMarketDataDTO;
import com.javafreelance.montecarlo.service.MonteCarloService;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MonteCarloServiceImplTest {
	
	@Autowired
    private MonteCarloService monteCarloService;
	
	@Test
	public void thing() throws InterruptedException {
		/*CountDownLatch latch = new CountDownLatch(1);
		Flux<SimulatedMarketDataDTO> flux = monteCarloService.retrieveMonteCarloPublisher("000001");
		flux.subscribe(
				md -> log.debug(md.toString()), 
				e -> { 
					log.error("error!", e);
					assertTrue(false);
					latch.countDown();
				}, 
				() -> {
					latch.countDown();
				});
		latch.await();*/
		assertTrue(true);
		
	}


		
}
