#!/bin/sh
type scalac >/dev/null 2>&1 || {
    echo "Cannot find 'scalac'. Make sure it is in PATH" > compilation.log
    exit 1
}
if [ ! -f $SCALA_HOME/lib/scala-library.jar ]
then
    echo Unable to find scala-library.jar in SCALA_HOME [$SCALA_HOME] > compilation.log
    exit 1
fi

if [ ! -f src/main/scala/Runner.scala ]
then
    echo Unable to find src/main/scala/Runner.scala > compilation.log
    exit 1
fi

if [ ! -f src/main/scala/MyStrategy.scala ]
then
    echo Unable to find src/main/scala/Runner.scala > compilation.log
    exit 1
fi

rm -rf classes
mkdir classes

scalac -sourcepath "src/main/scala" -d classes "src/main/scala/Runner.scala" > compilation.log

if [ ! -f classes/MyStrategy.class ]
then
    echo Unable to find classes/MyStrategy.class >> compilation.log
    exit 1
fi

if [ ! -f classes/Runner.class ]
then
    echo Unable to find classes/Runner.class >> compilation.log
    exit 1
fi

jar cf "./scala-cgdk.jar" -C "./classes" .
