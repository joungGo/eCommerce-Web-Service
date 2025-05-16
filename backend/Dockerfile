# 1. 베이스 이미지: JDK 환경 사용
FROM openjdk:21

# 2. 컨테이너 내부에 작업 디렉토리 생성
WORKDIR /app

# 3. Jar파일 복사 (빌드한 Spring Boot 앱)
COPY build/libs/eCommerce-Web-Service-0.0.1-SNAPSHOT.jar my-app.jar

# 4. 앱 실행 명령어
ENTRYPOINT ["java", "-jar", "my-app.jar"]