# Rest API Discovery

This project discovers the rest-endpoints and rest-clients from the bytecode a microservice. Given a set of compiled microservice artifacts, it also generate a rest flow graph among those endpoints and clients.


To get started clone the Github [repository](https://github.com/cloudhubs/rad).

```
$ git clone https://github.com/cloudhubs/rad.git
```

## Major Dependencies

- [Spring boot](https://spring.io/projects/spring-boot)
- [Javassist](https://github.com/jboss-javassist/javassist)
- [Local weaver](https://bitbucket.org/cilab/local-weaver/src/master/) (`rest` branch)
- [Apache commons](https://mvnrepository.com/artifact/org.apache.commons)
- [Lombok](https://projectlombok.org/)

## Run the Application

### Prepare the `Local weaver` library

```
$ git clone https://{username}@bitbucket.org/cilab/local-weaver.git
$ cd local-weaver
$ git checkout rest
$ mvn clean install -DskipTests
```

### Prepare the test bed 

We will use [CIL-TMS](https://bitbucket.org/cilab/cil-tms/src/master/) (`rad` branch) as our test bed.

```
$ git clone https://{username}@bitbucket.org/cilab/cil-tms.git
$ cd cil-tms
$ git checkout rad
```

Package each microservice.

```
$ cd tms-cms && mvn clean install -DskipTests
$ cd ../tms-ems && mvn clean install -DskipTests
$ cd ../tms-qms && mvn clean install -DskipTests
$ cd ../tms-qms && mvn clean install -DskipTests
```

### Compile and run the application

```
$ git clone https://github.com/cloudhubs/rad.git
$ cd rad
$ mvn clean install -DskipTests
$ java -jar application/target/rad-application-0.1.0.jar
```

### Sample request and response

```
curl --request POST \
  --url http://localhost:8080/ \
  --header 'content-type: application/json' \
  --data '{
    "pathToCompiledMicroservices":"C:\\seer-lab\\cil-tms",
    "organizationPath":"edu/baylor/ecs",
    "outputPath":"C:\\seer-lab\\cil-rad.gv"
}'
```

```
{
  "request": {
    "pathToCompiledMicroservices": "C:\\seer-lab\\cil-tms",
    "organizationPath": "edu/baylor/ecs",
    "outputPath": "C:\\seer-lab\\cil-rad.gv"
  },
  "restEntityContexts": [
    {
      "resourcePath": "C:\\seer-lab\\cil-tms\\tms-cms\\target\\cms-0.0.1-SNAPSHOT.jar",
      "restEntities": [
        {
          "url": "http://localhost:10003/categoryInfo",
          "applicationName": null,
          "ribbonServerName": null,
          "resourcePath": "C:\\seer-lab\\cil-tms\\tms-cms\\target\\cms-0.0.1-SNAPSHOT.jar",
          "className": "edu.baylor.ecs.cms.controller.CategoryInfoController",
          "methodName": "getCategoryInfo",
          "returnType": "java.util.List<java.lang.Object>",
          "path": "/categoryInfo",
          "httpMethod": "GET",
          "pathParams": null,
          "queryParams": null,
          "consumeType": null,
          "produceType": null,
          "client": false
        },
        ...
      ]
    },
    ...
  ],
  "restFlowContext": {
    "restFlows": [
      {
        "resourcePath": "C:\\seer-lab\\cil-tms\\tms-cms\\target\\cms-0.0.1-SNAPSHOT.jar",
        "className": "edu.baylor.ecs.cms.service.UmsService",
        "methodName": "isEmailValid",
        "servers": [
          {
            "url": "http://localhost:9004/userinfo/emailInUse/{email}",
            "applicationName": null,
            "ribbonServerName": null,
            "resourcePath": "C:\\seer-lab\\cil-tms\\tms-ums\\target\\ums-1.0-SNAPSHOT.jar",
            "className": "edu.baylor.ecs.ums.controller.UserInfoController",
            "methodName": "isEmailInUse",
            "returnType": null,
            "path": "/userinfo/emailInUse/{email}",
            "httpMethod": "GET",
            "pathParams": [
              {
                "name": "VARIABLE_NAME",
                "defaultValue": ""
              }
            ],
            "queryParams": null,
            "consumeType": null,
            "produceType": null,
            "client": false
          }
        ]
      },
      ...
    ]
  }
}
```
