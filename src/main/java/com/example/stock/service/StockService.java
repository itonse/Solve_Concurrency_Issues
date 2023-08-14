package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * @Transactional이 붙으면, 여러 스레드가 동시에 decrease 메서드에 접근하여 동시성 문제가 발생
     * synchronized를 사용하면 decrease 메서드에 한 번에 하나의 스레드만 접근
     *
     * + 그러나 서버가 한 대 일때는 데이터의 접근을 서버 한 대만 하기 때문에 문제X
     * 다중 서버일때는 데이터의 접근을 여러대에서 할 수 있다.
     */
    //@Transactional
    public synchronized void decrease(Long id, Long quantity) {    // synchronized 적용: 이 메소드는 한 개의 스레드만 접근 가능

        Stock stock = stockRepository.findById(id).orElseThrow();    // Stock 조회
        stock.decrease(quantity);   // 재고를 감소시킨 뒤

        stockRepository.saveAndFlush(stock);   // 갱신된 값을 저장
    }
}
