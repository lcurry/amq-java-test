## Overview

This is an example of how use the Java JMS api with ActiveMQ.

## Prereqs

- Install Java SDK
- Install [Maven](http://maven.apache.org/download.html) 
- Local Artemis 7.8 server installed and running with default listener 61616.  Allow anonymous user during setup (no user/pass required). 

## Building

Run:

    mvn install

## Running the Examples

In one terminal window run:


    java -cp target/openwire-example-0.1-SNAPSHOT.jar example.Publisher

In another terminal window run:

    java -cp target/openwire-example-0.1-SNAPSHOT.jar example.Listener


## Output 
You can see the following output 

You will see an error at the broker:

2024-07-05 11:51:52,011 WARN  [org.apache.activemq.artemis.core.client] AMQ212037: Connection failure to /127.0.0.1:60026 has been detected: AMQ229014: Did not receive data from /127.0.0.1:60026 within the 1,000ms connection TTL. The connection will now be closed. [code=CONNECTION_TIMEDOUT]

At at the Listener:

Consuming message.. 
Unexpected exception : 
javax.jms.IllegalStateException: The Consumer is closed
	at org.apache.activemq.ActiveMQMessageConsumer.checkClosed(ActiveMQMessageConsumer.java:879)
	at org.apache.activemq.ActiveMQMessageConsumer.receive(ActiveMQMessageConsumer.java:566)
	at example.Listener.main(Listener.java:61)


This seems to indicate the reconnect() (after broker has timed out the connection) is not connecting to the broker.
Expected behavior would be for the Listener to reconnect. 
