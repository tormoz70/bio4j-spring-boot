@echo off
SET AREYOUSURE=
SET /P AREYOUSURE=Are you sure run release (Y/[N])?
IF /I "%AREYOUSURE%" NEQ "Y" GOTO END
call mvn release:prepare -Darguments="-DskipTests -Dmaven.javadoc.skip=true"
IF NOT ERRORLEVEL 1 (
    call mvn release:perform -Darguments="-DskipTests -Dmaven.javadoc.skip=true"
)
:END