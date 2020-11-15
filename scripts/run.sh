#!/bin/bash                                                                                                                                                                                                

LOG=output.log
JAR=adstxt-0.0.8-standalone.jar

rm -f $LOG

export DATABASE_URL=postgresql://localhost/adstxt

nohup java -jar $JAR -p 4003 >$LOG 2>&1 &



