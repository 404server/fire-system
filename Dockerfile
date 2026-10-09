FROM gradle:9.7.1-jdk25 AS build
ENV GRADLE_USER_HOME=/gradle-cache
WORKDIR /src

COPY settings.gradle* build.gradle* gradle.properties* ./
COPY gradle/ gradle/
RUN gradle dependencies --no-daemon

COPY src/ src/
RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /src/build/libs/*.jar app.jar
ENV SERVER_PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
