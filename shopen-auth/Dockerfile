FROM amazoncorretto:22 AS runtime
WORKDIR /app
COPY build/libs/shopen-auth-all.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]