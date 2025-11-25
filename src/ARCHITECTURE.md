# Arquitetura do Flash Sale Engine

```
┌────────────────────┐
│     Cliente/Web     │
└─────────┬──────────┘
          │ HTTP (Compra)
          ▼
┌────────────────────┐
│    Producer API     │
│  (Spring Boot)      │
└─────────┬──────────┘
          │
          │ 1️⃣ Valida estoque no Redis
          ▼
┌────────────────────┐
│       Redis         │
│ (Contador de Stock) │
└─────────┬──────────┘
          │
          │ 2️⃣ Reserva confirmada → Envia evento
          ▼
┌────────────────────┐
│       Kafka         │
│  Topic: reservation │
│       -events       │
└─────────┬──────────┘
          │
          │ 3️⃣ Armazena a mensagem e garante ordem
          │    (mesmo userId = mesma partição)
          ▼
┌────────────────────┐
│ReservationConsumer  │
│   (Spring Boot)     │
└─────────┬──────────┘
          │
          │ 4️⃣ Processa no seu ritmo
          │    (backpressure natural do Kafka)
          ▼
┌────────────────────┐
│    PostgreSQL       │
│  (Pedidos com       │
│  idempotência SQL)  │
└────────────────────┘
```
