FROM java:8
VOLUME /tmp
EXPOSE 4444
ADD chili-config-server-1.0-SNAPSHOT.jar chili-config-server.jar
RUN bash -c 'touch /chili-config-server.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/chili-config-server.jar"]

