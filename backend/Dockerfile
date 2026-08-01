# syntax=docker/dockerfile:1

FROM maven:3.9.15-eclipse-temurin-26-alpine AS build

WORKDIR /workspace

COPY pom.xml ./
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src
RUN mvn --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:17-jre-alpine AS runtime

RUN addgroup -S -g 10001 linguacards \
    && adduser -S -D -H -u 10001 -G linguacards linguacards

WORKDIR /app

COPY --from=build \
    /workspace/target/linguacards-0.0.1-SNAPSHOT.jar \
    /app/linguacards.jar

USER 10001:10001

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=6 \
    CMD wget --quiet --spider http://127.0.0.1:8080/api/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/linguacards.jar"]
