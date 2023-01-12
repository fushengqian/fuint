#!/bin/sh

workdir=$(cd `dirname $0`;pwd)
cd $workdir || (echo "change to $workdir failed" && exit 1)

./stop.sh || { echo "stop server failed" && exit 1; }
sleep  1
./start.sh
