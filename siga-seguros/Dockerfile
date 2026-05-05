FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="SIGA Seguros"

WORKDIR /app

COPY target/siga-seguros-*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
