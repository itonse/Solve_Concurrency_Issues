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

    @Transactional
    public void decrease(Long id, Long quantity) {

        Stock stock = stockRepository.findById(id).orElseThrow();    // Stock 조회
        stock.decrease(quantity);   // 재고를 감소시킨 뒤

        stockRepository.saveAndFlush(stock);   // 갱신된 값을 저장
    }
}
