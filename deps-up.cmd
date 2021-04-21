@echo off

SET command=%1
SET arg1=%2

IF "%command%" == "" (
    echo Usage: %~n0%~x0 ^<command^> [arg]
    echo Commands:
    echo    check   - checks for available newer versions of dependencies
    echo    apply   - applies pom update to use latest available versions,
    echo                if 'arg' is specified then it is a package name to update ^(wildcards allowed^)
    echo    commit  - commits the changes in pom and deletes backup
    echo    revert  - reverts pom to its original state using backup
)
IF "%command%"=="apply" (
    IF "%arg1%"=="" (
        mvn versions:update-parent
        mvn versions:use-latest-versions
        mvn versions:update-properties
    ) ELSE (
        mvn versions:use-latest-versions -Dincludes=%arg1%
    )
)
IF "%command%"=="check" (
    mvn versions:display-dependency-updates -DprocessDependencyManagement=false -DprocessPluginDependenciesInPluginManagement=false -DprocessPluginDependencies=false
    mvn versions:display-parent-updates
)
IF "%command%"=="commit" (
    mvn versions:commit
)
IF "%command%"=="revert" (
    mvn versions:revert
)
