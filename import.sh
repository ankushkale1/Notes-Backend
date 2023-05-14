#!/bin/bash

mysql -h140.238.255.72 -uroot -ppassword@123 notesdb <$1

#basic setup
#https://phoenixnap.com/kb/how-to-create-new-mysql-user-account-grant-privileges
#sudo mysql –u root –p
#when prompted enter linux password
#now you will see mysql shell ie. mysql>
#CREATE USER 'mysqluser' IDENTIFIED BY 'password@123';
#GRANT ALL PRIVILEGES ON *.* TO 'mysqluser'
# create database notesdb
#eg mysql -h127.0.0.1 -umysqluser -ppassword@123 notesdb < 2022_01_08_1641644398.sql