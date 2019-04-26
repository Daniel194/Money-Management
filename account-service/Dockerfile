FROM openjdk:11-jdk

ADD ./build/libs/account-service-1.0-SNAPSHOT.jar /app/
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:6005", "-jar", "/app/account-service-1.0-SNAPSHOT.jar"]

EXPOSE 6000 6005