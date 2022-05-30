@echo off
set MODULE_NAME=%1%


if "%1%"=="" (
	echo "Usage : {kms|appv|print}"
	exit
) else (
	if "%MODULE_NAME%" == "kms" ( 
	echo module name : %MODULE_NAME%
	goto set_val
	) else if "%MODULE_NAME%" == "appv" ( 
	echo module name : %MODULE_NAME%
	goto set_val
	) else if "%MODULE_NAME%" == "print" (
	echo module name : %MODULE_NAME%
	goto set_val
	) else (
		echo "Usage : {kms|appv|print}"
		exit
	)
	

)
:set_val

set currDir=%cd%
cd ..
set parentDir=%cd%

set WA_MIG_PATH=%parentDir%
set WA_SYS_PATH=%WA_MIG_PATH%\conf
set WA_CONF_PATH=%WA_SYS_PATH%\module
set MODULE_PATH=%WA_CONF_PATH%\%MODULE_NAME%
set WA_MIG_LOG_FILE=%WA_MIG_PATH%\conf\logback.xml
set WA_MIG_LOG_PATH=%WA_MIG_PATH%\logs\%MODULE_NAME%
set WA_MIG_LOG_MODE=debug
set JAVA_OPTS=-Xms128m -Xmx1024m

set WA_MIGRATION_RUNTIME_OPTS=-Dsys.path=%WA_SYS_PATH% -Dconf.path=%WA_CONF_PATH%  -Dlogback.configurationFile=%WA_MIG_LOG_FILE% -DMODULE_PATH=%MODULE_PATH% -Dlog.path=%WA_MIG_LOG_PATH% -Dlog.mode=%WA_MIG_LOG_MODE% -Dmig.home=%WA_MIG_PATH%

echo "WA_MIG_PATH           : %WA_MIG_PATH%"
echo "WA_SYS_PATH           : %WA_SYS_PATH%"
echo "WA_CONF_PATH          : %WA_CONF_PATH%"
echo "MODULE_PATH           : %MODULE_PATH%"
echo "WA_MIG_LOG_FILE       : %WA_MIG_LOG_FILE%"
echo "WA_MIG_LOG_PATH       : %WA_MIG_LOG_PATH%"
echo "JAVA_OPTS             : %JAVA_OPTS%"
echo "WA_MIGRATION_RUNTIME_OPTS: %WA_MIGRATION_RUNTIME_OPTS%"
