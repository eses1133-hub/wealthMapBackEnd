FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN rm -rf .gradle
RUN chmod +x gradlew
RUN ./gradlew bootJar -x test --no-daemon
RUN mv build/libs/*.jar build/libs/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]