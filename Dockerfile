FROM openjdk:17-alpine
ENTRYPOINT ["/usr/bin/user-service.sh"]

COPY user-service.sh /usr/bin/user-service.sh
COPY target/user-service-0.0.1-SNAPSHOT.jar /usr/share/user-service/user-service.jar
