FROM openjdk:17-alpine
WORKDIR /app
COPY target/app-login-2.jar /app/app-login-2.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app-login-2.jar"]
