#!/bin/bash
cd dumps
dt=`date +%Y_%m_%d_%s`
mkdir $dt
cd $dt
mysqldump --column-statistics=0 -h127.0.0.1 -uroot -proot@123 notesdb > $dt.sql

gzip $dt.sql
