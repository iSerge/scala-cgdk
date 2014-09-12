@echo off
for %%X in (scalac.bat) do (set SCALA_FOUND=%%~$PATH:X)

if not exist "%SCALA_HOME%\scala-library.jar" (
    echo Unable to find 'scala-library.jar' in SCALA_HOME ["%SCALA_HOME%"]
    exit 1
)

if not exist src\main\scala\Runner.scala (
    echo Unable to find src\main\scala\Runner.scala > compilation.log
    exit 1
)

if not exist src\main\scala\MyStrategy.scala (
    echo Unable to find src\main\scala\MyStrategy.scala > compilation.log
    exit 1
)

rd /Q /S classes
md classes

scalac -encoding UTF-8 -sourcepath "src/main/scala" -d classes "src/main/scala/Runner.scala" > compilation.log

if not exist classes\Runner.class (
    echo Unable to find classes\Runner.class >> compilation.log
    exit 1
)

if not exist classes\MyStrategy.class (
    echo Unable to find classes\MyStrategy.class >> compilation.log
    exit 1
)

jar cvfe "./scala-cgdk.jar" Runner -C "./classes" .
