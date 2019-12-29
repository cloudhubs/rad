mkdir target

docker create -ti --name dummy ${TARGET_IMAGE} bash
docker cp dummy:/rad-sample.jar target/rad-sample.jar
docker rm -f dummy

java -jar rad-cli.jar
cat output.json
