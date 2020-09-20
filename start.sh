#!/bin/bash

docker-compose -f docker-compose-local.yml up -d mysqldb

java -Xverify:none -XX:+UseStringDeduplication -Xms32m \
-Xmx2048m \
-XX:MaxHeapFreeRatio=15 \
-XX:MinHeapFreeRatio=5 \
-XX:+UnlockExperimentalVMOptions \
-XX:+UseShenandoahGC \
-XX:ShenandoahUncommitDelay=10000 \
-XX:ShenandoahGuaranteedGCInterval=60000 \
-XX:CompressedClassSpaceSize=70M \
-XX:MetaspaceSize=16M \
-XX:MinMetaspaceFreeRatio=10 \
-XX:MaxMetaspaceFreeRatio=15 \
-XX:MinMetaspaceExpansion=1M \
-XX:MaxMetaspaceExpansion=5M \
-Xss500k \
-XX:NativeMemoryTracking=detail \
-XX:MaxGCPauseMillis=1000 -jar target/NotesApp-0.0.1.jar &
