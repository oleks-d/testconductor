#!/usr/bin/env

mvn clean install

echo "Copy files"

scp -i ${HOME}/Documents/AWS/aws_inka.pem \
  target/test-conductor-0.0.1-SNAPSHOT.jar \
  ec2-user@ec2-3-124-218-1.eu-central-1.compute.amazonaws.com:/home/ec2-user/test_conductor


echo "Restart server"

ssh -i ${HOME}/Documents/AWS/aws_inka.pem \
  ec2-3-124-218-1.eu-central-1.compute.amazonaws.com << EOF

pgrep java | sudo xargs kill -9
cd /home/ec2-user/test_conductor
sudo nohup java -jar test-conductor-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:h2:file:/home/ec2-user/data/test_conductorV3 --hostname=localhost:8080 --server.port=8080 > log$(date +%Y%m%d_%H%m).txt &

EOF

echo "Done"