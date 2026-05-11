FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY db-common/pom.xml db-common/pom.xml
COPY crawler/pom.xml crawler/pom.xml
COPY indexer/pom.xml indexer/pom.xml
COPY search-api/pom.xml search-api/pom.xml

RUN mvn dependency:go-offline -pl db-common,crawler -am -q

COPY db-common/src db-common/src
COPY crawler/src crawler/src
RUN mvn package -pl db-common,crawler -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/crawler/target/crawler-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]