#!/bin/bash                                                                                                                                  \
                                                                                                                                              
LOG=crawl.log
JAR=`find . -name adstxt-0.0.*-standalone.jar` 

export DATABASE_URL=postgresql://localhost/adstxt

nohup java -jar $JAR crawl >$LOG 2>&1 &

