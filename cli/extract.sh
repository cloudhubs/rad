mkdir target

docker create -ti --name dummy diptadas/rad-sample bash
docker cp dummy:/rad-sample.jar target/rad-sample.jar
docker rm -f dummy

java -jar rad-cli.jar
cat output.json
