FROM openjdk:11-jdk-slim

EXPOSE 8080

COPY ./build/libs/*.jar app.jar
COPY ../portfolio.pdf portfolio.pdf
#ARG PROFILE=prod
#ARG PROFILE=local
ENV PROFILE=${PROFILE}

ENTRYPOINT ["java","-Dspring.profiles.active=${PROFILE}", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]