#!/bin/bash

java -Xverify:none -XX:+UseStringDeduplication -Xms32m \
-Xmx2048m \
-XX:MaxHeapFreeRatio=15 \
-XX:MinHeapFreeRatio=5 \
-XX:+UnlockExperimentalVMOptions \
-XX:+UseShenandoahGC \
-XX:ShenandoahUncommitDelay=10000 \
-XX:ShenandoahGuaranteedGCInterval=60000 \
-XX:MaxGCPauseMillis=1000 -jar target/NotesApp-0.0.1.jar &
