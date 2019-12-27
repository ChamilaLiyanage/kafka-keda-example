# Extend vert.x image
FROM vertx/vertx3
ENV JAVA_APP_DIR=/deployments
COPY target/kafka-consumer-1.0-SNAPSHOT-shaded.jar /deployments/
ENTRYPOINT ["sh", "-c", "java -jar /deployments/kafka-consumer-1.0-SNAPSHOT-shaded.jar"]