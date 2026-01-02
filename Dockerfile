FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew && ./gradlew build -x test


CMD ["java", "-jar", "build/libs/itemmanagement-0.0.1-SNAPSHOT.jar"]
