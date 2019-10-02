#!/bin/bash

mkdir vtcs
cp ./command/target/scala-2.12/vtcs.jar ./vtcs
cp ./command/src/main/resources/application.conf ./vtcs
cp ./core/src/main/resources/db/migration/V1__init.sql ./vtcs
zip -r vtcs.zip vtcs
