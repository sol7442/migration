WA_MIG_PATH=`dirname $PWD`
WA_MIG_CONF="${WA_MIG_PATH}/conf/KMS_SAMPLE_01/conf.yml"
WA_MIG_LOG_FILE="${WA_MIG_PATH}/conf/logback.xml"
WA_MIG_LOG_PATH="${WA_MIG_PATH}/logs"
JAVA_OPTS="-Xms128m -Xmx1024m"

SCWA_MIG_SERVER_OPTS="-Dsys.path=${WA_MIG_CONF} -Dmig.home=${WA_MIG_PATH}"
echo "WA_MIG_PATH 	  : ${WA_MIG_PATH}"
echo "WA_MIG_LOG_PATH : ${WA_MIG_LOG_PATH}"
echo "WA_MIG_LOG_FILE : ${WA_MIG_LOG_FILE}"
echo "WA_MIG_CONF     : ${WA_MIG_CONF}"
echo "JAVA_OPTS : ${JAVA_OPTS}"
echo "WA_MIG_SERVER_OPTS : ${SCWA_MIG_SERVER_OPTS}"

export WA_MIG_PATH
export WA_MIG_LOG_PATH
export WA_MIG_LOG_FILE
export WA_MIG_HOME
export JAVA_OPTS
export WA_MIG_SERVER_OPTS