FROM openjdk:11-jdk

ADD ./build/libs/auth-service-1.0-SNAPSHOT.jar /app/
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-jar","/app/auth-service-1.0-SNAPSHOT.jar"]

EXPOSE 5000 5005