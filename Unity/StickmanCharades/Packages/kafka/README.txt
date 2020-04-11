In order to make kafka work on Windows, I had to:

1. remove the following lines from kafka.run-class.bat:

rem Classpath addition for kafka-streams-examples
for %%i in ("%BASE_DIR%\streams\examples\build\libs\kafka-streams-examples*.jar") do (
	call :concat "%%i"
)

rem Classpath addition for kafka-examples
for %%i in ("%BASE_DIR%\examples\build\libs\kafka-examples*.jar") do (
	call :concat "%%i"
)

for %%i in ("%BASE_DIR%\streams\build\dependant-libs-%SCALA_VERSION%\rocksdb*.jar") do (
	call :concat "%%i"
)

for %%i in ("%BASE_DIR%\tools\build\dependant-libs-%SCALA_VERSION%\*.jar") do (
	call :concat "%%i"
)

rem Classpath addition for core
for %%i in ("%BASE_DIR%\core\build\libs\kafka_%SCALA_BINARY_VERSION%*.jar") do (
	call :concat "%%i"
)

2. change the following lines from kafka.run-class.bat:
from:
	rem Classpath addition for release
	for %%i in ("%BASE_DIR%\libs\*") do (
		call :concat "%%i"
	)
to:
	rem Classpath addition for release
	call :concat "%BASE_DIR%\libs\*;"

