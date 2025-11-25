FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar ./techhatch-app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","techhatch-app.jar"]
