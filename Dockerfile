FROM eclipse-temurin:23-jdk AS builder

WORKDIR /app

COPY . .

# RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:23-jre

COPY . .

WORKDIR /app

COPY --from=builder /app/target/video-1.0-SNAPSHOT.jar video.jar
COPY --from=builder /app//src/main/resources/yt-dlt_binarie/yt-dlp yt-dlp

EXPOSE 8080


ENTRYPOINT [ "java","-jar", "video.jar" ]