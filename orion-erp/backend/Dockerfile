FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven \
    && mvn -B -DskipTests package \
    && mv target/*.jar app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S orion && adduser -S orion -G orion
COPY --from=build /app/app.jar .
USER orion
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
