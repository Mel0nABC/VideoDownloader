FROM archlinux:latest


RUN mkdir -p /opt/videodownloader/
COPY /target/video-1.0-SNAPSHOT.jar /opt/videodownloader/aplication.jar
COPY yt-dlp /opt/videodownloader/yt-dlp

RUN pacman -Sy --noconfirm
RUN pacman -S jre-openjdk --noconfirm


EXPOSE 8080

RUN ["pacman","-Syu","--noconfirm"]
RUN ["pacman","-S","ffmpeg","--noconfirm"]

WORKDIR /opt/videodownloader/
RUN chmod u+x yt-dlp

ENTRYPOINT ["java","-jar","aplication.jar"]
CMD ["/usr/bin/bash"]