#!/bin/bash
CLASSPATH=target/test-classes:target/marionette-2.0-SNAPSHOT-jar-with-dependencies.jar 
if [[ ! "$(uname)" =~ ^Linux.* ]]
then
  sep=";"
  CLASSPATH=${CLASSPATH//:/$sep}
  echo "CLASSPATH: $CLASSPATH"
fi
mvn clean package assembly:single -U -DskipTests=true && \
"$JAVA_HOME/bin/java" --add-modules jdk.incubator.httpclient,java.xml.bind -jar ~/.m2/repository/org/junit/platform/junit-platform-console-standalone/1.1.0-M1/junit-platform-console-standalone-1.1.0-M1.jar -classpath "$CLASSPATH" -m com.swgas.rest.WebDriverServiceTest#testGetCookie
