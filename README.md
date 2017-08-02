# spring-cloud-service-discovery-demo

First, run `docker-compose up -d` and check `localhost:8080`.
Second, run `./gradlew dockerBuild` in **demo00-test-marathon-app** project.
Third, deploy and delete test app:
```
http POST localhost:8080/v2/apps?force=true < test-marathon-app-manifest.json
http DELETE localhost:8080/v2/apps/test-marathon-app?force=true
```

Then go through demos and launch `./gradlew demoXX-XXX:bootRun` for each of them.
