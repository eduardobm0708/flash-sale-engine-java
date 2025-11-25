package com.flashsale.messaging;

import com.flashsale.dto.ReservationRequest;
import com.flashsale.entity.Reservation;
import com.flashsale.repository.ReservationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReservationConsumer {

    private final ReservationRepository reservationRepository;

    // Injeção via construtor
    public ReservationConsumer(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @KafkaListener(topics = "reservation-events", groupId = "flashsale-group")
    public void consume(ReservationRequest request) {

        // 1. Converter o DTO em uma entidade Reservation
        Reservation reservation = new Reservation(
                request.userId(),
                request.itemId(),
                request.quantity()
        );

        try {
            // 2. Tentar salvar no banco
            reservationRepository.save(reservation);
            System.out.println("Reserva salva com sucesso: " + reservation);

        } catch (DataIntegrityViolationException e) {
            // 3. Mensagem duplicada → ignorar sem crashar o consumer
            System.out.println("Mensagem duplicada detectada: " + request);
        }
    }
}
