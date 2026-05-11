# Kafka Sharing Knowledge - User Registration Demo

This project demonstrates an **Apache Kafka** implementation using **Spring Boot WebFlux** and **Reactor Kafka**. It demonstrates how to produce user registration events to a Kafka topic asynchronously and non-blockingly.

## 📋 Daftar Isi

- [Tech stack](#tech-stack)
- [Simple Architecture](#simple-architecture)
- [Prerequisite](#prerequisite)
- [Configuration](#configuration)
- [How to run](#how-to-run)
- [Endpoint API](#endpoint-api)
- [Project Structure](#project-structure)
- [Flow Event](#event-flow)
- [Kafka Monitoring](#kafka-monitoring)
- [Testing](#testing)
- [License](#license)


## 🚀 Tech stack

| Tech | Version | Utility |
|-----------|-------|----------|
| Spring Boot | 3.5.x | Core Framework |
| Spring WebFlux | - | Reactive REST API |
| Reactor Kafka | - | Kafka Producer reactive |
| Apache Kafka with Zookeeper | 3.7.x | Message broker |
| Project Lombok | - | Boilerplate code reduction |
| Jackson | - | JSON serialization |


## 🏗️ Simple Architecture

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

## 📦 Prerequisite

Before undertaking this project, make sure you have:

1. **Java 21** or newer
2. **Apache Kafka with zookeeper** (You may use docker or local installation)
3. **Maven**

### Running Kafka inside Docker (Optional)

You can have only 1 Kafka broker or 3 Kafka brokres

# run Zookeeper
```docker run -d --name zookeeper -p 2181:2181 zookeeper:latest```

# run Kafka
```docker run -d --name kafka \
  -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=host.docker.internal:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:latest
```

## ⚙️ Configuration

Configuration via application.yml or environment variables.

Application Properties

```
server:
  port: 9023  # SERVER_PORT

kafka:
  kafka-properties:
    bootstrap-servers: localhost:9092

app:
  topics:
    registration-topic: demo-user-registrations
    approval-topic: demo-approval-status
```

### Environment Variables

|Variable                 | Default | Description |
|-------------------------|---------|-------------|
|SERVER_PORT | 9023 | Port of application | 
|KAFKA_BOOTSTRAP_SERVERS | localhost:9092 | Address of Kafka broker |


## 🏃 How to run

1. Clone and open the project
``` 
git clone https://github.com/kafka-lesson/registration-service.git 
```

```
cd your-project-directory
```

2. Make sure Kafka is running

bash
# Check Kafka connection
```
nc -zv localhost 9092
```

3. Run the app

Maven:
```
./mvnw spring-boot:run
```

4. Check logs

The application prints this in console:

```
2024-01-15 10:30:00 INFO  --- [main] c.y.RUserRegistrationApplication : Started
2024-01-15 10:30:00 INFO  --- [main] --- Netty started on port 9023
```

## 📡 Endpoint API

Register User

```
Endpoint: POST /api/users/register
```

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

## 📁 Project Structure

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

## 🔄 Event Flow

1. Client sends POST request to ``` /api/users/register ```
2. RegisterController receives request and create UserRegisteredEvent using new UUID
3. RegistrationProducer does:
   · Serialize events to JSON using Jackson
   · Create a ProducerRecord with key = userId, value = JSON payload
   · Sending to Kafka topic demo-user-registrations using Reactor Kafka
4. Kafka stores events in topics
5. Simple Response is returned to the client (reactive, non-blocking)

## 📊 Kafka Monitoring

To check the available topics

```
kafka-topics --bootstrap-server localhost:9092 --list
```

Consume messages from a topic

```
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic demo-user-registrations \
  --from-beginning
```

Sample output:

```
{"userId":"123e4567-e89b-12d3-a456-426614174000","age":25,"email":"john@example.com","username":"john_doe"}
```

## 🧪 Testing

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

## License

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

👨‍💻 Author

Yusuf RH - Sharing Knowledge Session - Kafka with Reactive Spring
