# 1. Gradle을 이용하여 빌드
FROM gradle:8.12.1-jdk17 AS builder
WORKDIR /app

# Gradle 캐시 활용을 위해 build.gradle과 settings.gradle을 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY . .
RUN gradle build --no-daemon -x test

# 2. OpenJDK 17을 이용하여 실행

FROM openjdk:17-jdk AS runtime
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar /app.jar

# 실행
ENTRYPOINT ["java", "-Duser.timezone=GMT+9", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]
