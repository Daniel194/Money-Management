FROM openjdk:11-jdk

ADD ./build/libs/statistics-service-1.0-SNAPSHOT.jar /app/
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:7005", "-jar", "/app/statistics-service-1.0-SNAPSHOT.jar"]

EXPOSE 7000 7005