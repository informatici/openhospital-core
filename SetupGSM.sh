#!/bin/sh
#
# Open Hospital (www.open-hospital.org)
# Copyright © 2006-2018 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
#
# Open Hospital is a free and open source software for healthcare data management.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# https://www.gnu.org/licenses/gpl-3.0-standalone.html
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.
#

######## Environment check:

# check for java home
JAVA_EXE=$JAVA_HOME/bin/java

if [ -z $JAVA_HOME ]; then
  echo "JAVA_HOME not found. Please set it up properly."
  JAVA_EXE=java
fi

######## OPEN HOSPITAL Configuration:

# add the libraries to the OPENHOSPITAL_CLASSPATH.
# EXEDIR is the directory where this executable is.
EXEDIR=${0%/*}

DIRLIBS=${EXEDIR}/bin/*.jar
for i in ${DIRLIBS}
do
  if [ -z "$OPENHOSPITAL_CLASSPATH" ] ; then
    OPENHOSPITAL_CLASSPATH=$i
  else
    OPENHOSPITAL_CLASSPATH="$i":$OPENHOSPITAL_CLASSPATH
  fi
done


DIRLIBS=${EXEDIR}/lib/*.jar
for i in ${DIRLIBS}
do
  if [ -z "$OPENHOSPITAL_CLASSPATH" ] ; then
    OPENHOSPITAL_CLASSPATH=$i
  else
    OPENHOSPITAL_CLASSPATH="$i":$OPENHOSPITAL_CLASSPATH
  fi
done

DIRLIBS=${EXEDIR}/lib/h8/*.jar
for i in ${DIRLIBS}
do
  if [ -z "$OPENHOSPITAL_CLASSPATH" ] ; then
    OPENHOSPITAL_CLASSPATH=$i
  else
    OPENHOSPITAL_CLASSPATH="$i":$OPENHOSPITAL_CLASSPATH
  fi
done

DIRLIBS=${EXEDIR}/lib/dicom/*.jar
for i in ${DIRLIBS}
do
  if [ -z "$OPENHOSPITAL_CLASSPATH" ] ; then
    OPENHOSPITAL_CLASSPATH=$i
  else
    OPENHOSPITAL_CLASSPATH="$i":$OPENHOSPITAL_CLASSPATH
  fi
done

DIRLIBS=${EXEDIR}/lib/dicom/dcm4che/*.jar
for i in ${DIRLIBS}
do
  if [ -z "$OPENHOSPITAL_CLASSPATH" ] ; then
    OPENHOSPITAL_CLASSPATH=$i
  else
    OPENHOSPITAL_CLASSPATH="$i":$OPENHOSPITAL_CLASSPATH
  fi
done

DIRLIBS=${EXEDIR}/lib/dicom/jai/*.jar
for i in ${DIRLIBS}
do
  if [ -z "$OPENHOSPITAL_CLASSPATH" ] ; then
    OPENHOSPITAL_CLASSPATH=$i
  else
    OPENHOSPITAL_CLASSPATH="$i":$OPENHOSPITAL_CLASSPATH
  fi
done

DIRLIBS=${EXEDIR}/lib/*.zip
for i in ${DIRLIBS}
do
  if [ -z "$OPENHOSPITAL_CLASSPATH" ] ; then
    OPENHOSPITAL_CLASSPATH=$i
  else
    OPENHOSPITAL_CLASSPATH="$i":$OPENHOSPITAL_CLASSPATH
  fi
done

OPENHOSPITAL_CLASSPATH="${EXEDIR}/../classes":$OPENHOSPITAL_CLASSPATH
OPENHOSPITAL_CLASSPATH="${EXEDIR}/bundle":$OPENHOSPITAL_CLASSPATH
OPENHOSPITAL_CLASSPATH="${EXEDIR}":$OPENHOSPITAL_CLASSPATH
OPENHOSPITAL_HOME="${EXEDIR}"

ARCH=$(uname -m)
case $ARCH in
	x86_64|amd64|AMD64)
		NATIVE_LIB_PATH=${OPENHOSPITAL_HOME}/lib/native/Linux/amd64
		;;
	i[3456789]86|x86|i86pc)
		NATIVE_LIB_PATH=${OPENHOSPITAL_HOME}/lib/native/Linux/i386
		;;
	*)
		echo "Unknown architecture $(uname -m)"
		;;
esac

$JAVA_EXE -Djava.library.path=${NATIVE_LIB_PATH} -classpath "$OPENHOSPITAL_CLASSPATH:$CLASSPATH" org.isf.utils.sms.SetupGSM "$@"
