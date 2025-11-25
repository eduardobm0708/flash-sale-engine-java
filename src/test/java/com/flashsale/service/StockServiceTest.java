package com.flashsale.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate; // O Dublê do Redis

    @Mock
    private RedisScript<Long> script;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter; // O Dublê do Contador

    @InjectMocks
    private StockService stockService; // A classe real que vamos testar

    @Test
    void deveDecrementarEstoqueComSucesso() {
        // 1. Prepara o cenário (Given)
        String itemId = "iphone_15";

        when(meterRegistry.counter("flashsale.sold")).thenReturn(counter);

        when(redisTemplate.execute(eq(script), any(List.class), any(String.class)))
                .thenReturn(1L);

        // 2. Executa a ação (When)
        boolean resultado = stockService.tryDecreaseStock(itemId, 1);

        // 3. Verifica o resultado (Then)
        assertTrue(resultado);
        verify(counter).increment();
    }


    // ⭐ ESTE TESTE PRECISA ESTAR DENTRO DA CLASSE
    @Test
    void deveRetornarFalsoQuandoSemEstoque() {
        // 1. Prepara (Given)
        when(meterRegistry.counter("flashsale.rejected")).thenReturn(counter);

        when(redisTemplate.execute(eq(script), any(List.class), any(String.class)))
                .thenReturn(0L);

        // 2. Executa (When)
        boolean resultado = stockService.tryDecreaseStock("iphone_15", 1);

        // 3. Verifica (Then)
        assertFalse(resultado);
        verify(counter).increment();
    }
}
