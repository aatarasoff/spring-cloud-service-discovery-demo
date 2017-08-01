# Deploy and delete test app
http POST localhost:8080/v2/apps?force=true < test-marathon-app-manifest.json
http DELETE localhost:8080/v2/apps/test-marathon-app?force=true

# Change env variable
http -v --form POST host:port/env TEST_APP_DELAY=1000

# Some load on services
http http://localhost:9090/feign && echo "GET http://localhost:9090/feign" | vegeta attack -rate=20 -duration=20s | tee results.bin | vegeta report