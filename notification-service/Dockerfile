FROM openjdk:11-jdk

ADD ./build/libs/notification-service-1.0-SNAPSHOT.jar /app/
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8005", "-jar", "/app/notification-service-1.0-SNAPSHOT.jar"]

EXPOSE 8000 8005