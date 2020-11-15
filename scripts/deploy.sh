#!/bin/bash

echo "--------------------------------------------------"
echo "deploy adstxt"

scp ./target/adstxt-0.0.8-standalone.jar blucas@beaconhill.com:/home/blucas/apps/adstxt

scp ./scripts/run.sh blucas@beaconhill.com:/home/blucas/apps/adstxt
scp ./scripts/crawl.sh blucas@beaconhill.com:/home/blucas/apps/adstxt

echo "done"
