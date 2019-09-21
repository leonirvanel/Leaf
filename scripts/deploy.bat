@echo off

cd d:\work\Leaf\leaf-client
call mvn clean deploy -DskipTests

cd d:\work\Leaf
call mvn clean install -DskipTests
scp leaf-server\target\leaf.jar root@10.11.117.37:/root/leaf/
scp leaf-server\target\leaf.jar root@10.11.117.38:/root/leaf/
scp leaf-server\target\leaf.jar root@10.11.117.39:/root/leaf/

cd d:\work\messageproxy
call mvn clean package -DskipTests
scp target\messageproxy-1.0-SNAPSHOT.jar root@10.11.117.32:/root/server/application/messageproxy/default/messageproxy.jar
scp target\messageproxy-1.0-SNAPSHOT.jar root@10.11.117.33:/root/server/application/messageproxy/default/messageproxy.jar
scp target\messageproxy-1.0-SNAPSHOT.jar root@10.11.117.34:/root/server/application/messageproxy/default/messageproxy.jar
scp target\messageproxy-1.0-SNAPSHOT.jar root@10.11.117.35:/root/server/application/messageproxy/default/messageproxy.jar
scp target\messageproxy-1.0-SNAPSHOT.jar root@10.11.117.36:/root/server/application/messageproxy/default/messageproxy.jar

