#!/bin/bash

MODULE_NAME=${1}
PROCESS_ID=`cat "$MODULE_NAME".pid`
echo kill process : $PROCESS_ID

#kill -15 $PROCESS_ID

rm "$MODULE_NAME".pid