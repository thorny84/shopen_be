FROM amazoncorretto:22 AS runtime
WORKDIR /app
COPY build/libs/shopen-api-gateway-all.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]