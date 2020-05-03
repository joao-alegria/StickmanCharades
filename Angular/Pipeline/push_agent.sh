#!/bin/sh

docker build -t 192.168.160.99:5000/esp54-web_pipeline_agent -f AgentDockerfile .
docker push 192.168.160.99:5000/esp54-web_pipeline_agent
