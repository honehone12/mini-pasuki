#!/bin/bash

SPRING_DATASOURCE_URL="jdbc:mysql://localhost:30036/dbname" \
SPRING_DATASOURCE_USERNAME="" \
SPRING_DATASOURCE_PASSWORD="" \
SPRING_JPA_HIBERNATE_DDL_AUTO="update" \
./gradlew bootRun
