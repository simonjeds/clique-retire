FROM openjdk:11

ARG PROFILE_SPRING
ENV PROFILE_SPRING ${PROFILE_SPRING}

VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} cliqueretire.jar
CMD /bin/sh -c "java -Xmx${JAVA_XMX:=2g} -Dspring.profiles.active=$PROFILE_SPRING -jar /cliqueretire.jar"