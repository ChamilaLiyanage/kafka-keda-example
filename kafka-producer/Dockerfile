# Extend vert.x image
FROM vertx/vertx3

ENV VERTICLE_NAME com.examples.java.vertx.kafka.producer.ProducerVerticle
ENV VERTICLE_FILE target/kafka-producer-1.0-SNAPSHOT-shaded.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8090

# Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]