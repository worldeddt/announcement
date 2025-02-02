FROM ubuntu:18.04


RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get install -y redis-tools && \
    apt-get clean;


WORKDIR /app

EXPOSE 8080

COPY build/libs/announcement.jar /app/announcement.jar

CMD ["java", "-jar", "announcement.jar"]