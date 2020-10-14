FROM adoptopenjdk:11-jre-hotspot
COPY target/helmholtz-cerebrum-*.jar app.jar
EXPOSE 8090
CMD java -Dspring.profiles.active=dev -jar app.jar --springdoc.swagger-ui.oauth.client-secret=$CLIENT_SECRET
