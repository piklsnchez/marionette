#!/bin/bash
mvn clean package assembly:single -DskipTests=true && \
$JAVA_HOME/bin/java -jar ~/.m2/repository/org/junit/platform/junit-platform-console-standalone/1.0.0-RC3/junit-platform-console-standalone-1.0.0-RC3.jar -classpath target/test-classes:target/marionette-2.0-SNAPSHOT-jar-with-dependencies.jar -m com.swgas.marionette.MarionetteImplTest#testFindElements
