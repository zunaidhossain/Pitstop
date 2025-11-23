# 🚗 **PitStop Backend**

### Spring Boot · MongoDB · JWT Security · Razorpay Payments · OTP Workflow

---

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-blue" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-brightgreen" />
  <img src="https://img.shields.io/badge/MongoDB-Atlas-success" />
  <img src="https://img.shields.io/badge/Razorpay-Integrated-blue" />
  <img src="https://img.shields.io/badge/Build-Passing-success" />
</p>

---

## 📌 **Overview**

PitStop is a backend system powering an on-demand automobile servicing platform. It manages:

* User & workshop authentication
* Real-time booking lifecycle
* OTP-secured state transitions
* Secure online payments via Razorpay
* Fault-tolerant refund logic
* Workshop redirection & failure recovery

This backend is designed with **production-grade patterns**, **strict business rules**, and a clear **state machine-based workflow**.

---

## 🛠️ **Technology Stack**

### **Backend**

* Java 21
* Spring Boot 3+
* Spring Security (JWT Authentication)
* Spring Web
* Spring Data MongoDB
* Lombok
* Razorpay Java SDK

### **Database**

* MongoDB (Atlas recommended)

### **Build Tools**

* Maven

### **Testing**

* JUnit 5
* Integration Testing

---

## 🧩 **System Architecture**

### 🔹 High-Level Modules

1. **Authentication Module** (JWT)
2. **Booking Engine** (State Machine)
3. **Payment Engine** (Razorpay, Refund Logic)
4. **OTP & Workflow Validation**
5. **Workshop Management**
6. **User Address & Location Module**

### 🔹 Architectural Style

* Modular service-layer driven design
* Entity → DTO → Controller flow
* Strong validation & business rule enforcement
* Repository abstraction for MongoDB
* Clean separation of **Payment Logic** and **Refund Logic**

---

## 🔐 **Authentication & Authorization**

* **JWT-based login** for both AppUsers & WorkshopUsers
* Role-based flow enforcement
* Only AppUsers can initiate/complete payments
* Booking and Payment ownership checks prevent tampering

---

## ⚙️ **Booking Flow (State Machine)**

```
STARTED → BOOKED → ON_THE_WAY → WAITING → REPAIRING → COMPLETED
```

### **Key Rules**

* Workshop must accept booking to move STARTED → BOOKED
* User must start journey to move BOOKED → ON_THE_WAY
* OTP required for transitions:

  * ON_THE_WAY → WAITING
  * WAITING → REPAIRING
  * REPAIRING → COMPLETED
* **Booking cannot move to WAITING unless Payment = SUCCESS**

---

## 💳 **Payment System (Razorpay)**

### **Payment Allowed Only In:**

* `BOOKED`
* `ON_THE_WAY`

### **Flow**

1. **POST /api/payments/initiate/{bookingId}**

   * Authenticated AppUser only
   * Creates internal `Payment` record
   * Creates Razorpay Order
   * Returns `orderId`, `keyId`, `amount` (paise)

2. **Frontend Razorpay Checkout**

   * User pays via UPI/Card/Wallet

3. **POST /api/payments/complete**

   * Backend verifies Razorpay signature
   * Marks payment `SUCCESS` or `FAILED`

4. **Booking cannot reach WAITING unless payment = SUCCESS**

---

## 💰 **Refund Engine (Strict Business Logic)**

Refund depends on:

* Booking status
* Who cancelled
* Whether payment existed (SUCCESS)
* Business rules (OTP vs No OTP)

### **Refund Rules Table**

| Status     | Canceller | Refund? | OTP?  | Notes                    |
| ---------- | --------- | ------- | ----- | ------------------------ |
| STARTED    | AppUser   | ✅ Yes   | ❌ No  | Payment success required |
| STARTED    | Workshop  | ✅ Yes   | ❌ No  | Redirect AppUser         |
| BOOKED     | AppUser   | ❌ No    | ❌ No  | User backs out           |
| BOOKED     | Workshop  | ✅ Yes   | ✅ Yes | Workshop fault           |
| ON_THE_WAY | AppUser   | ❌ No    | ❌ No  | User already traveling   |
| ON_THE_WAY | Workshop  | ✅ Yes   | ✅ Yes | Workshop cancels         |
| WAITING    | AppUser   | ❌ No    | ❌ No  | No payment allowed here  |
| WAITING    | Workshop  | ❌ No    | ❌ No  | No payment allowed here  |

Refund only happens when:

```
paymentStatus = SUCCESS AND verified = true
```

---

## 📁 **Project Structure**

```
backend/
 └── src/main/java/com/pitstop/app/
      ├── controller/
      ├── service/
      ├── repository/
      ├── model/
      ├── payment/
      ├── dto/
      ├── security/
      └── util/
```

---

## 🚀 **Key API Endpoints**

### 🔐 **Auth**

```
POST /api/auth/register
POST /api/auth/login
```

### 📘 **Booking**

```
POST /api/booking/request/{workshopId}
GET  /api/booking/status/{bookingId}
POST /api/booking/startJourney/{bookingId}
POST /api/booking/generateOtp/{bookingId}
POST /api/booking/verifyOtp
POST /api/booking/cancel/{bookingId}
```

### 💳 **Payments**

```
POST /api/payments/initiate/{bookingId}
POST /api/payments/complete
```

---

## 🧪 **Testing Strategy**

* Full lifecycle tests (Started → Completed)
* OTP verification tests
* Payment & refund validation
* Unauthorized access tests
* Repository integration tests

---

## 🧭 **Roadmap / Future Enhancements**

* WebSockets-based live location tracking
* Redis caching for booking queues
* Kafka/RabbitMQ for real-time notifications
* Admin dashboard for settlement & reconciliation
* Recommendation engine for nearest workshops

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome.
Before major changes, please open an issue to discuss your proposal.

---

## 📜 License

This project is licensed under the **MIT License**.

---
