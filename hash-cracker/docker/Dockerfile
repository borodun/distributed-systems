FROM gradle:8-jdk17 AS builder

ARG SERVICE

WORKDIR /
COPY settings.gradle /
COPY $SERVICE/build.gradle /$SERVICE/
COPY $SERVICE/src /$SERVICE/src

RUN gradle --no-daemon $SERVICE:build

FROM openjdk:17-jdk-alpine

ARG SERVICE
WORKDIR /$SERVICE

COPY --from=builder /$SERVICE/build/libs/$SERVICE.jar /$SERVICE/app.jar
COPY $SERVICE/src/main/resources/application.properties /$SERVICE/application.properties

ARG SERVICE_PORT
EXPOSE $SERVER_PORT

CMD ["java", "-jar", "app.jar", "--spring.config.location=classpath:/application.properties"]
