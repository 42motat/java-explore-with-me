FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/main-app-service-*.jar ewm.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ewm.jar"]