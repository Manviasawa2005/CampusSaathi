FROM tomcat:11.0-jdk21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY CampusSaathi.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
