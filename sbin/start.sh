#!/bin/sh
rm -f tpid

java  -Dfile.encoding=UTF-8 -Xmx2048m -Xms2048m -Xss256k -Xmn1024m -jar $1 &

echo $! > tpid

echo Start Success!
