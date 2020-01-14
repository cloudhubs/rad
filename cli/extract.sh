set -x

mkdir target

TARGET_JAR=$(docker inspect ${TARGET_IMAGE} | jq -r '.[0].Config.Entrypoint[-1]')

docker create -ti --name dummy ${TARGET_IMAGE} bash
docker cp dummy:${TARGET_JAR} target/target.jar
docker rm -f dummy

java -jar rad-cli.jar
cat output.json
