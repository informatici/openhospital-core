@REM
@REM Open Hospital (www.open-hospital.org)
@REM Copyright © 2006-2019 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
@REM
@REM Open Hospital is a free and open source software for healthcare data management.
@REM
@REM This program is free software: you can redistribute it and/or modify
@REM it under the terms of the GNU General Public License as published by
@REM the Free Software Foundation, either version 3 of the License, or
@REM (at your option) any later version.
@REM
@REM https://www.gnu.org/licenses/gpl-3.0-standalone.html
@REM
@REM This program is distributed in the hope that it will be useful,
@REM but WITHOUT ANY WARRANTY; without even the implied warranty of
@REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
@REM GNU General Public License for more details.
@REM
@REM You should have received a copy of the GNU General Public License
@REM along with this program. If not, see <http://www.gnu.org/licenses/>.
@REM

@echo off
set OH_HOME=%~dps0
REM if java is not in the system path set JAVA_HOME variable
REM set JAVA_HOME=%OH_HOME%jvm\bin

for %%i in (java.exe) do set JAVA=%%~s$PATH:i

IF NOT DEFINED JAVA (
	@echo Java not found
	EXIT /B
)

set OH_BIN=%OH_HOME%bin
set OH_LIB=%OH_HOME%lib
set OH_BUNDLE=%OH_HOME%bundle

set CLASSPATH=%OH_BIN%

SETLOCAL ENABLEDELAYEDEXPANSION

FOR %%A IN (%OH_LIB%\*.jar) DO (
	set CLASSPATH=!CLASSPATH!;%%A
)

FOR %%A IN (%OH_LIB%\dicom\*.jar) DO (
	set CLASSPATH=!CLASSPATH!;%%A
)

FOR %%A IN (%OH_LIB%\dicom\dcm4che\*.jar) DO (
	set CLASSPATH=!CLASSPATH!;%%A
)

FOR %%A IN (%OH_LIB%\dicom\jai\*.jar) DO (
	set CLASSPATH=!CLASSPATH!;%%A
)

set CLASSPATH=%CLASSPATH%;%OH_BUNDLE%
set CLASSPATH=%CLASSPATH%;%OH_BIN%;%OH_BIN%\OH.jar

IF (%PROCESSOR_ARCHITECTURE%)==(AMD64) (set NATIVE_PATH=%OH_LIB%\native\Win64) ELSE (set NATIVE_PATH=%OH_LIB%\native\Windows)

cd /d %OH_HOME%\

REM set JAVA=C:\PROGRA~2\Java\jdk1.6.0_29\bin\java.exe
REM start /min %JAVA_HOME%\java -showversion -Djava.library.path=%NATIVE_PATH% -classpath %CLASSPATH% org.isf.utils.SetupGSM
%JAVA% -showversion -Djava.library.path=%NATIVE_PATH% -classpath %CLASSPATH% org.isf.utils.sms.SetupGSM