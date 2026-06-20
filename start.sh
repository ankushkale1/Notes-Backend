#!/bin/bash

docker compose -f docker-compose-local.yml up -d mysqldb

# Kill any existing instance of the app before starting
pkill -f NotesApp-0.0.1.jar || true

#java -Xverify:none \
#  -XX:+UseStringDeduplication \
#  -Xms64m \
#  -Xmx768m \
#  -XX:MaxHeapFreeRatio=20 \
#  -XX:MinHeapFreeRatio=10 \
#  -XX:+UnlockExperimentalVMOptions \
#  -XX:+UseShenandoahGC \
#  -XX:ShenandoahUncommitDelay=5000 \
#  -XX:ShenandoahGuaranteedGCInterval=30000 \
#  -XX:CompressedClassSpaceSize=64M \
#  -XX:MetaspaceSize=32M \
#  -XX:MinMetaspaceFreeRatio=10 \
#  -XX:MaxMetaspaceFreeRatio=20 \
#  -Xss256k \
#  -XX:MaxGCPauseMillis=500 \
#  -jar target/NotesApp-0.0.1.jar > app.log 2>&1 &


  # for Graal VM
exec java --enable-preview \
    -XX:+UseSerialGC \
    -Xms32m \
    -Xmx400m \
    -XX:MaxMetaspaceSize=160M \
    -XX:MaxHeapFreeRatio=30 \
    -XX:MinHeapFreeRatio=10 \
    -XX:MaxDirectMemorySize=64M \
    -Xss256k \
    -Dspring.main.lazy-initialization=true \
    -Dspring.jmx.enabled=false \
    -Dspring.threads.virtual.enabled=true \
    -jar /home/ankush/NotesApp-BackEnd/target/NotesApp-0.0.1.jar

echo "NoteBook App starting on port 8999..."
