#!/bin/bash

cd /home/ubuntu/docker
sudo docker-compose down
sudo docker-compose build
sudo docker image prune -f
sudo docker-compose up -d
