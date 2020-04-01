FROM java:8-jre

MAINTAINER huangll99@126.com
ADD target/scheduler-1.0.jar /app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
