# exchange-application

Is a web project used for currency exchange and financial transactions.

Used Technologies

  - Spring Boot
  - Java 11
  - JPA
  - Docker
  - H2
  - Swagger UI
  - JUnit - JMockit

We will need to create an image to make it up as docker compose.

> mvn spring-boot:build-image

After the image is created, it will be enough to write docker-compose up -d to the terminal.

> docker-compose up -d

Swagger Url 

> localhost:8080/swagger-ui.html

Note : Postman collections is under the dock folder.
