FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar ./

EXPOSE 8080

ENTRYPOINT ["java","-jar","techhatch-0.0.1-SNAPSHOT.jar"]
