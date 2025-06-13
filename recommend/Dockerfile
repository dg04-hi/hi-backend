# Recommend 서비스용 Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY common common/
COPY recommend recommend/

# 실행 권한 부여 및 빌드
RUN chmod +x ./gradlew
RUN ./gradlew recommend:build -x test --no-daemon

# 실행 단계
FROM eclipse-temurin:21-jre-alpine

# 애플리케이션 사용자 생성
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/recommend/build/libs/recommend-*.jar app.jar

# 파일 소유권 변경
RUN chown -R appuser:appgroup /app

# 사용자 변경
USER appuser

# 포트 노출 (Recommend 서비스는 8085 포트)
EXPOSE 8085

# 헬스체크 추가 (포트 8085로 변경)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8085/actuator/health || exit 1

# JVM 옵션 설정 (추천 서비스는 더 많은 메모리 필요)
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]