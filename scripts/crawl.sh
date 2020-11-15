#!/bin/bash                                                                                                                                  \
                                                                                                                                              
LOG=crawl.log
JAR=adstxt-0.0.8-standalone.jar

export DATABASE_URL=postgresql://localhost/adstxt

nohup java -jar $JAR crawl >$LOG 2>&1 &

