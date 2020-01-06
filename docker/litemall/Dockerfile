FROM openjdk:8-jre
COPY application.yml /litemall/application.yml
COPY litemall.jar /litemall/litemall.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","litemall.jar"]