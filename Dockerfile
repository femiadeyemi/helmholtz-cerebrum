FROM adoptopenjdk:11-jre-hotspot
ENV NEO4J_ADDRESS localhost
COPY target/helmholtz-cerebrum-*.jar cerebrum.jar
EXPOSE 8090
CMD java -Dspring.profiles.active=dev -jar cerebrum.jar --org.neo4j.driver.uri="bolt://${NEO4J_ADDRESS}:7687"
