FROM adoptopenjdk:11-jre-hotspot
COPY target/helmholtz-cerebrum-*.jar app.jar
EXPOSE 8090
CMD java -jar app.jar
