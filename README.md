# tredbase_payment_processing

# Family Payment System

This Spring Boot application manages payments between parents and students, supporting multi-parent relationships and dynamic processing fees. It tracks balances, logs transactions, and ensures secure and atomic operations during payment processing.

---

##  Features

-  Parents can make payments to students.
-  Supports shared payments between two assigned parents.
-  Dynamic rate (processing fee) is calculated and applied.
-  Balances for both parents and students are updated accurately.
-  All payments are persisted for tracking and auditing.
-  Secure access to resources using Spring Security.

---

## 🚀 How to Build, Run, and Test

### 🛠️ Build the Project

Ensure you have:
- Java 17+
- Maven 3.4+
- An IDE like IntelliJ or VS Code (optional)

- Security Design Decisions
This application uses Spring Security with in-memory authentication:

Credentials (username/password) is externalized in application.properties for security and flexibility.

Only authenticated users can access API endpoints (can be configured with role-based  (admin) authorization).

- Payment Logic & Balance Arithmetic
  Dynamic Rate Logic (adjustedAmount = paymentAmount * (1 + dynamicRate))

- Multi-Parent Payment Handling
  If a student has two assigned parents:

  The adjustedAmount is split equally.

  Each parent must have at least adjustedAmount / 2 in balance.

  Each is deducted exactly half of the total cost.

- Tables & Entity Relationships
  Parent (One-to-Many with Students)

  Student (Many-to-One/Many-to-Many with Parents)

  Payment logs every transaction, linked to Parent and Student

- API Endpoints
  Endpoint	Method	Description
  http://localhost:8080/api/parents 	 GET	Get all parents
  http://localhost:8080/api/payments	 POST	Process a parent-to-student payment
  http://localhost:8080/api/parent/{id}	 GET	Get parent by ID
  http://localhost:8080/api/payments/all GET    Get all payments

- Error Handling
  Checks for insufficient balance per parent before processing

  Throws exceptions with descriptive messages

  Uses @Transactional to ensure all DB changes succeed or none are committed

- Acknowledgements
  Spring Boot

  Spring Data JPA

  Spring Security

  H2 database for data persistence

```bash
# Clone the repository
git clone https://github.com/Suzidat12/tredbase_payment_processing


# Build the project
mvn clean install

# Run with Maven
mvn spring-boot:run

