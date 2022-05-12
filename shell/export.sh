#!/bin/sh
MODULE_NAME=${1}
echo ${MODULE_NAME}

if [ -z "${MODULE_NAME}" ]; then
  echo "MODULE NAME is empty"
  echo "process stoped.."
  exit
else
  case ${1} in
    kms   ) echo "kms start"   ;;
    appv  ) echo "appv start"  ;;
    print ) echo "print start" ;;
    *) echo "Invalid module name - {kms , appv , print}"
    exit ;;
    esac
fi

WA_MIG_PATH=`dirname $PWD`
WA_SYS_PATH="${WA_MIG_PATH}/conf"
WA_CONF_PATH="${WA_SYS_PATH}/module"
MODULE_PATH="${WA_CONF_PATH}/${MODULE_NAME}"
WA_SYSTEM_PROPERTIES="${WA_SYS_PATH}/system.properties"
WA_MIG_LOG_FILE="${WA_MIG_PATH}/conf/logback.xml"
JAVA_HOME="/home/im/jdk-8u302-ojdkbuild-linux-x64"
JAVA_OPTS="-Xms128m -Xmx1024m"
WA_MIG_SERVER_OPTS="-Dsys.path=${WA_SYS_PATH} -Dconf.path=${WA_CONF_PATH} -Dsystem.properties=${WA_SYSTEM_PROPERTIES} -Dlogback.configurationFile=${WA_MIG_LOG_FILE}"

echo "MODULE_PATH     : ${MODULE_PATH}"
echo "WA_MIG_PATH     : ${WA_MIG_PATH}"
echo "WA_SYS_PATH     : ${WA_SYS_PATH}"
echo "WA_CONF_PATH    : ${WA_CONF_PATH}"
echo "WA_SYSTEM_PROPERTIES    : ${WA_SYSTEM_PROPERTIES}"
echo "WA_MIG_LOG_FILE : ${WA_MIG_LOG_FILE}"
#echo "WA_MIG_SERVER_OPTS : ${WA_MIG_SERVER_OPTS}"

echo "JAVA_HOME : ${JAVA_HOME}"

export MODULE_PATH
export WA_MIG_PATH
export WA_SYS_PATH
export WA_CONF_PATH
export WA_SYSTEM_PROPERTIES
export WA_MIG_LOG_FILE
export WA_MIG_SERVER_OPTS
export JAVA_HOME
