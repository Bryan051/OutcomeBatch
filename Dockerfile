FROM openjdk:17-jdk-slim

# JAR 파일을 저장할 디렉토리를 설정합니다.
WORKDIR /app

# JAR 파일을 복사합니다.
COPY ./build/libs/OutcomeBatch-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 포트를 노출합니다.
EXPOSE 8080

# 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]