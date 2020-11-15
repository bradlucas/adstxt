#!/bin/bash                                                                                                                                                                                                

LOG=output.log
JAR=`find . -name adstxt-0.0.*-standalone.jar` 

rm -f $LOG

export DATABASE_URL=postgresql://localhost/adstxt

nohup java -jar $JAR -p 4003 >$LOG 2>&1 &



