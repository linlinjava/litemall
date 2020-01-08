FROM openjdk:8-jre
COPY application.yml application.yml
COPY litemall.jar litemall.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","litemall.jar"]