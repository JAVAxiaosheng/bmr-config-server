# shellcheck disable=SC2164
# shellcheck disable=SC1001
DIR_ROOT="D:/bmr-config-server"
JAR_DIR="bmr-config-app/target"
JAR_FILE="bmr-config-app-1.0.0-SNAPSHOT.jar"
SERVER_PORT=4649

cd $DIR_ROOT

mvn clean package

while [ ! -f "$DIR_ROOT/$JAR_DIR/$JAR_FILE" ]; do
  # shellcheck disable=SC1072
#  echo "$DIR_ROOT\\$JAR_DIR"
  pwd
  sleep 1
done

java -jar $DIR_ROOT/$JAR_DIR/$JAR_FILE --server.port=$SERVER_PORT