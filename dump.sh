#!/bin/bash
cd dumps
dt=$(date +%Y_%m_%d_%s)
mkdir $dt
cd $dt

mysqldump -h127.0.0.1 -uroot -proot@123 \
  --single-transaction \
  --quick \
  --max-allowed-packet=1G \
  --net-buffer-length=16384 \
  --routines \
  --triggers \
  --events \
  --default-character-set=utf8mb4 \
  --add-drop-table \
  --complete-insert \
  notesdb > $dt.sql

gzip $dt.sql
