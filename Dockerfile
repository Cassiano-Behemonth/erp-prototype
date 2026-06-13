# Estágio de compilação (Build)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio de execução (Run)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# A porta padrão configurada no Spring Boot (mas o Render/Railway vai definir a porta dinamicamente via variável $PORT)
EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
