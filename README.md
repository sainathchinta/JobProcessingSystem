# Job Processing System

A **High-Performance Task Queue & Job Processing System** built with **Spring Boot, Kafka, Redis, and PostgreSQL**.  

This system handles **long-running tasks** like report generation, email sending, or data export **asynchronously**, ensuring your application remains responsive and scalable.

---

## Features

- Accepts **job requests asynchronously**  
- Queues jobs for **efficient processing**  
- Handles **retries and failures**  
- Provides **fast status lookups** via caching  
- Persists **job metadata in PostgreSQL**  
- Supports a **Dead Letter Queue** for permanently failed jobs  
- Demonstrates **microservices patterns, async processing, and production-grade architecture**

---

## 🏛 Architecture

```text
      +----------------+
      |     Client     |
      | (Postman/GUI)  |
      +-------+--------+
              |
              v
     +-------------------+
     |   Job API Service |
     |   (Spring Boot)   |
     +--------+----------+
              |
              v
       Kafka Topic: job-queue
              |
              v
     +-------------------+
     |   Worker Service  |
     |   (Spring Boot)   |
     +--------+----------+
              |
 +------------+------------+
 |                         |
 v                         v
+------------+ +-------------+
| PostgreSQL | | Redis       |
| Job Table  | | Job Status  |
+------------+ +-------------+
              |
              v
   Dead Letter Queue (Kafka Topic)



## **🔄 Job Status Lifecycle**

The system tracks jobs through a robust state machine:

* **QUEUED** → **IN_PROGRESS** → **COMPLETED**
* **IN_PROGRESS** → **FAILED** → **RETRYING** → **DEAD_LETTER**

> **Note:** Failed jobs are retried up to **3 times**. If all retries fail, the job is moved to the **Dead Letter Queue (DLQ)**.

---

## **💾 Database Schema**

### **Table: jobs**

| Column | Type | Description |
| :--- | :--- | :--- |
| job_id | UUID | Primary key |
| job_type | String | Type of task (e.g., REPORT_GENERATION) |
| payload_json | Text | Original input payload |
| status | Enum | Current stage (QUEUED, FAILED, etc.) |
| retries | Int | Number of retry attempts |
| result_json | Text | Final job output |
| error_message | String | Error if job failed |
| created_at | Timestamp | Job creation time |
| updated_at | Timestamp | Last update time |

---

## **🛠 Tech Stack**

* **Backend:** Spring Boot, Java 21
* **Messaging:** Kafka
* **Database:** PostgreSQL
* **Cache:** Redis
* **Build Tool:** Maven
* **Documentation:** Swagger / OpenAPI

---

## **⚙️ Setup & Run**

### **Prerequisites**

Make sure **Redis** and **Kafka** are running in the background.

### **Instructions**

```bash
# 1. Clone the repository
git clone [https://github.com/username/JobProcessingSystem.git](https://github.com/username/JobProcessingSystem.git)
cd JobProcessingSystem

# 2. Build the project
./mvnw clean install

# 3. Run the Spring Boot application
./mvnw spring-boot:run
📖 API Documentation
Once the app is running, you can access the Swagger UI at:

👉 http://localhost:8080/swagger-ui.html
