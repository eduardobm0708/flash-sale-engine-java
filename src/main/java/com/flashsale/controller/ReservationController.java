package com.flashsale.controller;

import com.flashsale.dto.ReservationRequest;
import com.flashsale.messaging.ReservationProducer; // <--- Importante: Importamos o Producer
import com.flashsale.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reserve")
public class ReservationController {

    private final StockService stockService;
    private final ReservationProducer reservationProducer; // <--- 1. Novo campo para o Kafka

    // 2. Atualizamos o construtor para receber o Producer também
    public ReservationController(StockService stockService, ReservationProducer reservationProducer) {
        this.stockService = stockService;
        this.reservationProducer = reservationProducer;
    }

    @PostMapping
    public ResponseEntity<String> reserve(@RequestBody ReservationRequest request) {

        boolean success = stockService.tryDecreaseStock(
                request.itemId(),
                request.quantity()
        );

        if (success) {
            // --- AQUI ESTAVA FALTANDO! ---
            // Enviamos o evento para o Kafka salvar no banco depois
            reservationProducer.sendReservationEvent(request);
            // -----------------------------

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Reserva em processamento");
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Estoque esgotado");
    }
}