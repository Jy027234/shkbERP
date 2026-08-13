FROM eclipse-temurin:25-jre-ubi10-minimal

WORKDIR /opt

ARG JAR_FILE=@build.finalName@.jar
COPY ${JAR_FILE} /opt/app.jar

EXPOSE 15000

ENV JAVA_OPTS="-Dspring.profiles.active=prod"

CMD ["sh", "-c", "exec java -server $JAVA_OPTS -jar app.jar"]
