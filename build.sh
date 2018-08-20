javac -d ./build ./src/com/cvezga/influxdbwriter/*.java
cd build
jar cvf ../televibra.jar com/cvezga/influxdbwriter/*
