@ECHO OFF
SET DIR=%~dp0
SET CLASSPATH=%DIR%gradle\wrapper\gradle-wrapper.jar
IF DEFINED JAVA_HOME SET JAVACMD=%JAVA_HOME%\bin\java.exe
IF NOT DEFINED JAVACMD SET JAVACMD=java.exe
"%JAVACMD%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
