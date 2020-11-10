FROM adoptopenjdk:11-jre-hotspot
COPY target/helmholtz-cerebrum-*.jar app.jar
EXPOSE 8090
CMD java -Dspring.profiles.active=dev -jar app.jar -DclientSecret=$CLIENT_SECRET
