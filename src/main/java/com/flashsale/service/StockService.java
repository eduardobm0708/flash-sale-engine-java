package com.flashsale.service;

import io.micrometer.core.instrument.MeterRegistry; // [Novo] Import necessário
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class StockService {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> scriptDeEstoque;
    private final MeterRegistry meterRegistry; // [Novo] Campo para as métricas

    // [Atualizado] Injeção de dependência via construtor (agora com 3 itens)
    public StockService(StringRedisTemplate redisTemplate,
                        RedisScript<Long> scriptDeEstoque,
                        MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.scriptDeEstoque = scriptDeEstoque;
        this.meterRegistry = meterRegistry;
    }

    public boolean tryDecreaseStock(String itemId, int quantity) {
        List<String> keys = Collections.singletonList(itemId);

        Long result = redisTemplate.execute(
                scriptDeEstoque,
                keys,
                String.valueOf(quantity)
        );

        // [Atualizado] Lógica para contar sucesso ou falha
        if (result != null && result == 1L) {
            // Sucesso: Incrementa "vendas"
            meterRegistry.counter("flashsale.sold").increment();
            return true;
        } else {
            // Falha: Incrementa "rejeições" (estoque vazio)
            meterRegistry.counter("flashsale.rejected").increment();
            return false;
        }
    }
}