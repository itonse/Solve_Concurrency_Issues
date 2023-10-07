package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private PessimisticLockStockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void 재고감소() {
        stockService.decrease(1L, 1L);

        // 100 - 1 = 99

        Stock stock = stockRepository.findById(1L)
                .orElseThrow();

        assertEquals(99, stock.getQuantity());

    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        // 스레드 개수 설정
        int threadCount = 100;

        // 32개의 스레드로 구성된 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 연산이 끝나길 기다리기 위한 CountDownLatch 객체 생성
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 100번의 연산을 스레드 풀에 제출
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 재고 감소 서비스 호출 (1번 ID의 재고를 1만큼 감소)
                    stockService.decrease(1L, 1L);
                } finally {
                    // 작업이 완료되면 latch의 카운트를 1 감소
                    latch.countDown();
                }
            });
        }

        // 모든 작업이 완료될 때까지 대기
        latch.await();

        // 1번 ID의 재고 정보 가져오기
        Stock stock = stockRepository.findById(1L).orElseThrow();

        // 기대한 결과 확인: 100개의 감소 요청 후 재고는 0이어야 함
        assertEquals(0, stock.getQuantity());

    }
}