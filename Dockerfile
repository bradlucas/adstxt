FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/adstxt.jar /adstxt/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/adstxt/app.jar"]
