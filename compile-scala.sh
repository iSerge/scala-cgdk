#!/bin/sh
if [ ! -f src/main/scala/Runner.scala ]
then
    echo Unable to find src/main/scala/Runner.scala > compilation.log
    exit 1
fi

rm -rf classes
mkdir classes

scalac -sourcepath "src/main/scala" -d classes "src/main/scala/Runner.scala" > compilation.log

if [ ! -f classes/Runner.class ]
then
    echo Unable to find classes/Runner.class >> compilation.log
    exit 1
fi

jar cf "./scala-cgdk.jar" -C "./classes" .
