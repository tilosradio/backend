#!/usr/bin/env bash

mvn clean install -DskipTests

#TAG=$(next_docker_image.sh tilosradio/episode)
TAG=quay.io/tilosradio/backend
docker build -t $TAG .
docker push $TAG
