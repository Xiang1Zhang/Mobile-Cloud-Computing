 
cd Client
chmod +x gradlew
./gradlew clean
./gradlew cleanBuildCache
./gradlew build --stacktrace

cd ../Server
npm install
gcloud endpoints services deploy openapi-appengine.yaml
gcloud app deploy