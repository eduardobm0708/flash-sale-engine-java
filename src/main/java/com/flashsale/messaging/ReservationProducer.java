package com.flashsale.messaging;

import com.flashsale.dto.ReservationRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReservationProducer {

    private final KafkaTemplate<String, ReservationRequest> kafkaTemplate;

    public ReservationProducer(KafkaTemplate<String, ReservationRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendReservationEvent(ReservationRequest request) {
        kafkaTemplate.send("reservation-events", request.userId(), request);
        System.out.println("Evento enviado para o Kafka: " + request);
    }
}