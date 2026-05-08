# Kafka Sharing Knowledge - User Registration Demo

Proyek ini adalah demonstrasi implementasi **Apache Kafka** menggunakan **Spring Boot WebFlux** dan **Reactor Kafka**. Proyek ini menunjukkan bagaimana cara memproduksi event user registration ke Kafka topic secara asinkron dan non-blocking.

## 📋 Daftar Isi

- [Teknologi yang Digunakan](#teknologi-yang-digunakan)
- [Arsitektur Sederhana](#arsitektur-sederhana)
- [Prasyarat](#prasyarat)
- [Konfigurasi](#konfigurasi)
- [Cara Menjalankan](#cara-menjalankan)
- [Endpoint API](#endpoint-api)
- [Struktur Proyek](#struktur-proyek)
- [Alur Event](#alur-event)

## 🚀 Teknologi yang Digunakan

| Teknologi | Versi | Kegunaan |
|-----------|-------|----------|
| Spring Boot | 4.x | Framework utama |
| Spring WebFlux | - | Reactive REST API |
| Reactor Kafka | - | Kafka Producer reactive |
| Apache Kafka | 3.7.x | Message broker |
| Project Lombok | - | Boilerplate code reduction |
| Jackson | - | JSON serialization |

## 🏗️ Arsitektur Sederhana

```text
┌─────────────┐          ┌──────────────┐         ┌─────────────┐
│   Client    │───────▶  │  Controller  │───────▶│  Producer   │
│  (POST /    │          │  (Reactive)  │         │   (Kafka)   │
│  register)  │          └──────────────┘         └──────┬──────┘
└─────────────┘                                          │
                                                         ▼
                                                 ┌───────────────┐
                                                 │  Kafka Topic  │
                                                 │  demo-user-   │
                                                 │ registrations │
                                                 └───────────────┘
```

## 📦 Prasyarat

Sebelum menjalankan proyek ini, pastikan Anda memiliki:

1. **Java 21** atau lebih baru
2. **Apache Kafka** (bisa menggunakan Docker atau instalasi lokal)
3. **Maven** atau **Gradle** (sesuai project build tool)

### Menjalankan Kafka dengan Docker (Opsional)

# Jalankan Zookeeper
```docker run -d --name zookeeper -p 2181:2181 zookeeper:latest```

# Jalankan Kafka
```docker run -d --name kafka \
  -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=host.docker.internal:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:latest
```
⚙️ Konfigurasi

Konfigurasi aplikasi dapat diatur melalui application.yml atau environment variables.

Application Properties

```
server:
  port: 9023  # atau via SERVER_PORT

kafka:
  kafka-properties:
    bootstrap-servers: localhost:9092

app:
  topics:
    registration-topic: demo-user-registrations
    approval-topic: demo-approval-status
```

Environment Variables

Variable Default Deskripsi
SERVER_PORT 9023 Port aplikasi
KAFKA_BOOTSTRAP_SERVERS localhost:9092 Alamat Kafka broker

🏃 Cara Menjalankan

1. Clone / Buka project
``` 
git clone https://github.com/kafka-lesson/registration-service.git 
```

```
cd your-project-directory
```

2. Pastikan Kafka sedang berjalan

bash
# Cek koneksi Kafka
```
nc -zv localhost 9092
```

3. Jalankan aplikasi Spring Boot

Maven:
```
./mvnw spring-boot:run
```

4. Cek log aplikasi

Aplikasi akan menampilkan log seperti:

```
2024-01-15 10:30:00 INFO  --- [main] c.y.RUserRegistrationApplication : Started
2024-01-15 10:30:00 INFO  --- [main] --- Netty started on port 9023
```

📡 Endpoint API

Register User

Endpoint: POST /api/users/register

Request Body:

```
{
  "age": 25,
  "email": "john.doe@example.com",
  "username": "johndoe"
}
```

Response:
```
· Success (200): "User registration initiated and sent to Kafka stream!"
· Error (200): "Kafka send failed: <error message>"
```

Contoh menggunakan cURL:

```
curl -X POST http://localhost:9023/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "age": 25,
    "email": "john@example.com",
    "username": "john_doe"
  }'
```

📁 Struktur Proyek

```
src/
├── main/
│   ├── java/com/yusufrh/
│   │   ├── config/
│   │   │   ├── KafkaConfig.java          # Kafka configuration properties
│   │   │   ├── KafkaProducerConfig.java  # Reactor Kafka sender config
│   │   │   └── AppProperties.java        # App custom properties
│   │   ├── controller/
│   │   │   └── RegisterController.java   # REST endpoint handler
│   │   ├── entity/
│   │   │   └── UserRegisteredEvent.java  # Event data model
│   │   └── service/
│   │       └── RegistrationProducer.java # Kafka producer service
│   └── resources/
│       └── application.yml                # Application configuration
```

🔄 Alur Event

1. Client mengirim POST request ke /api/users/register
2. RegisterController menerima request dan membuat UserRegisteredEvent dengan UUID baru
3. RegistrationProducer melakukan:
   · Serialisasi event ke JSON menggunakan Jackson
   · Membuat ProducerRecord dengan key = userId, value = JSON payload
   · Mengirim ke Kafka topic demo-user-registrations menggunakan Reactor Kafka
4. Kafka menyimpan event di topic
5. Response dikembalikan ke client (reactive, non-blocking)

📊 Monitoring Kafka

Melihat topic yang tersedia

```
kafka-topics --bootstrap-server localhost:9092 --list
```

Mengonsumsi pesan dari topic

```
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic demo-user-registrations \
  --from-beginning
```

Output contoh:

```
{"userId":"123e4567-e89b-12d3-a456-426614174000","age":25,"email":"john@example.com","username":"john_doe"}
```

🧪 Testing

Unit Test (contoh)

```
@Test
void testPublishRegistrationEvent() {
    UserRegisteredEvent event = new UserRegisteredEvent(
        UUID.randomUUID(), 25, "test@email.com", "testuser"
    );
    
    StepVerifier.create(registrationProducer.publishRegistrationEvent(event))
        .verifyComplete();
}
```

🐛 Troubleshooting

Masalah Solusi
Connection refused ke Kafka Pastikan Kafka running di localhost:9092
Topic tidak ada Kafka akan auto-create topic (default)
Serialization error Pastikan UserRegisteredEvent memiliki getter/setter
Reactor Kafka timeout Cek koneksi jaringan dan restart Kafka

📚 Referensi

· Apache Kafka Documentation
· Reactor Kafka GitHub
· Spring WebFlux Documentation

📝 Catatan Penting

· Proyek ini tanpa consumer - hanya fokus ke Producer untuk sharing knowledge
· Untuk consumer implementation, bisa ditambahkan @KafkaListener atau Reactor Kafka consumer
· Gunakan environment variables untuk konfigurasi production

👨‍💻 Author

Yusuf RH - Sharing Knowledge Session - Kafka with Reactive Spring

---

