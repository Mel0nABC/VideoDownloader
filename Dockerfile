FROM eclipse-temurin:23-jdk AS builder

WORKDIR /app

COPY . .

# FROM eclipse-temurin:23-jre

# COPY . .

# WORKDIR /app

# COPY --from=builder /app/target/video-1.0-SNAPSHOT.jar video.jar

COPY /target/video-1.0-SNAPSHOT.jar video.jar

RUN ["wget","https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux"]
RUN ["mv","yt-dlp_linux","yt-dlp"]
RUN ["chmod","u+x","yt-dlp"]

EXPOSE 8080

ENTRYPOINT [ "java","-jar", "video.jar" ]