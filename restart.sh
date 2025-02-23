# shellcheck disable=SC2164
DIR_ROOT="D:/bmr-config-server"
TARGET_FILE="start.sh"

cd $DIR_ROOT

if [ -f $DIR_ROOT/$TARGET_FILE ]; then
    powershell.exe $DIR_ROOT/$TARGET_FILE
fi
