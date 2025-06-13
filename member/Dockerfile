# member/Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사 (common 먼저 복사해서 캐시 효율성 향상)
COPY common/ ./common/
COPY member/ ./member/

# 실행 권한 부여 및 빌드
RUN chmod +x ./gradlew
RUN ./gradlew member:build -x test --no-daemon

# 실행 단계
FROM eclipse-temurin:21-jre-alpine

# 애플리케이션 사용자 생성
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# 필요한 패키지 설치
RUN apk add --no-cache curl

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/member/build/libs/member-*.jar app.jar

# 파일 소유권 변경
RUN chown -R appuser:appgroup /app

# 사용자 변경
USER appuser

# 포트 노출 (member 서비스 포트)
EXPOSE 8081

# 헬스체크 추가
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# JVM 옵션 설정
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"
ENV SPRING_PROFILES_ACTIVE=docker

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]