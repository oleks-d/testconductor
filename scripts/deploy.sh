#!/usr/bin/env

mvn clean install

echo "Copy files"

scp -i ${HOME}/Documents/AWS/aws_inka.pem \
  target/test-conductor-0.0.1-SNAPSHOT.jar \
  ec2-user@ec2-3-124-218-1.eu-central-1.compute.amazonaws.com:/home/ec2-user/test_conductor

scp -i ${HOME}/Documents/AWS/aws_inka.pem \
  /Users/odiachuk/data/test_conductorV3.* \
  ec2-user@ec2-3-124-218-1.eu-central-1.compute.amazonaws.com:/home/ec2-user/data

scp -i ${HOME}/Documents/AWS/aws_inka.pem ec2-user@ec2-3-124-218-1.eu-central-1.compute.amazonaws.com:/home/ec2-user/data/test_conductorV2.* /Users/odiachuk/data2/

echo "Restart server"

ssh -i ${HOME}/Documents/AWS/aws_yahoo.pem \
  ec2-user@ec2-18-190-28-132.us-east-2.compute.amazonaws.com << EOF

pgrep java | xargs kill -9
cd /home/ec2-user/test_conductor
nohup java -jar test-conductor-0.0.1-SNAPSHOT.jar > log.txt &

EOF

echo "Done"