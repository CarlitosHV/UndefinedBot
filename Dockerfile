FROM gradle:8.11-jdk17 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./

COPY gradle ./gradle

COPY gradlew gradlew.bat ./

RUN gradle dependencies --no-daemon || true

COPY src ./src

RUN gradle shadowJar --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/undefined-bot-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
