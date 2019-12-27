# Extend vert.x image
FROM vertx/vertx3
ENV JAVA_APP_DIR=/deployments
ENV VERTICLE_FILE target/kafka-producer-1.0-SNAPSHOT-shaded.jar
EXPOSE 8090
COPY target/kafka-producer-1.0-SNAPSHOT-shaded.jar /deployments/
ENTRYPOINT ["sh", "-c", "java -jar /deployments/kafka-producer-1.0-SNAPSHOT-shaded.jar"]