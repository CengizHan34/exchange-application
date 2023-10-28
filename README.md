# exchange-application

Is a web project used for currency exchange and financial transactions.

Used Technologies

- Spring Boot
- Java 17
- JPA
- H2
- JUnit - JMockit

The project runs at > localhost:8080

One of the three endpoints defined brings the current foreign exchange price. 
Secondly, it exchanges the money you send according to the current price. 
The third one allows you to view these transactions.

Transactions are carried out by pulling the current foreign exchange prices from a different service.

Note : To run your tests, you can use the postman collection that I added to the doc folder.
