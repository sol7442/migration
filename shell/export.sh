#!/bin/sh
MODULE_NAME=${1}
echo ${MODULE_NAME}

if [ -z "${MODULE_NAME}" ]; then
    echo $"Usage : $0 {kms|appv|print}"
    exit 1
else
  case ${1} in
    kms   ) echo "kms start"   ;;
    appv  ) echo "appv start"  ;;
    print ) echo "print start" ;;
    *) 
    echo $"Usage : $0 {kms|appv|print}"
    exit 1
    esac
fi

WA_MIG_PATH=`dirname $PWD`
WA_SYS_PATH="${WA_MIG_PATH}/conf"
WA_CONF_PATH="${WA_SYS_PATH}/module"
MODULE_PATH="${WA_CONF_PATH}/${MODULE_NAME}"
WA_MIG_LOG_FILE="${WA_SYS_PATH}/logback.xml"
WA_MIGRATION_RUNTIME_OPTS="-Dsys.path=${WA_SYS_PATH} -Dconf.path=${WA_CONF_PATH} -Dlogback.configurationFile=${WA_MIG_LOG_FILE}"
JAVA_OPTS="-Xms128m -Xmx1024m"
JAVA_HOME="/home/im/jdk-8u302-ojdkbuild-linux-x64"

echo "WA_MIG_PATH        : ${WA_MIG_PATH}"
echo "WA_SYS_PATH        : ${WA_SYS_PATH}"
echo "WA_CONF_PATH       : ${WA_CONF_PATH}"
echo "MODULE_PATH        : ${MODULE_PATH}"
echo "WA_MIG_LOG_FILE    : ${WA_MIG_LOG_FILE}"
echo "WA_MIGRATION_RUNTIME_OPTS : ${WA_MIGRATION_RUNTIME_OPTS}"
echo "JAVA_HOME          : ${JAVA_HOME}"


export WA_MIG_PATH
export WA_SYS_PATH
export WA_CONF_PATH
export MODULE_PATH
export WA_MIG_LOG_FILE
export WA_MIG_SERVER_OPTS
export JAVA_HOME
