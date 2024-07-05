## Overview

This is an example of how use the Java JMS api with ActiveMQ.

## Prereqs

- Install Java SDK
- Install [Maven](http://maven.apache.org/download.html) 
- Local Artemis 7.8 server installed and running with default listener 61616.  Allow anonymous user during setup (no user/pass required). 


## What the demo does

This demo is built from the examples that ship with ActiveMQ classic 5.15.x.   

The Listener class has been re-written  to utilize some of the "Advanced InactivityMonitor Configuration" features in Openwire.
See the link in documenation for this feature [here.](https://activemq.apache.org/components/classic/documentation/activemq-classic-inactivitymonitor)

The demo makes use of connection ttl properties the detect inactivity and tell the broker to end the connection if the connection is idle for longer than the client has configured 'maxInactivityDuration' time. The Listener "forces" a connection close from the broker by adding a "sleep" into the code for greater than the configured maxInactivityDuration. 

The Publisher in this example is only used to load up the test queue with messages. The Listener is the class that consumes and forces the issue to occur. 

## Building


This repo can be cloned into a working directory. 

Run:

    mvn install

## Running the Examples

In one terminal window run the following.  


    java -cp target/openwire-example-0.1-SNAPSHOT.jar example.Publisher

This should send messages to load up a test queue on the running Artemis 7 broker. If successful you should see similar output:

```
[lcurry@fedora java]$  java -cp target/openwire-example-0.1-SNAPSHOT.jar example.Publisher
Sent 1000 messages
Sent 2000 messages
Sent 3000 messages
Sent 4000 messages
Sent 5000 messages
Sent 6000 messages
Sent 7000 messages
Sent 8000 messages
Sent 9000 messages
Sent 10000 messages
```

In another terminal window run:

    java -cp target/openwire-example-0.1-SNAPSHOT.jar example.Listener


## Output 
You can see the following output 

You will see an error at the broker:

```
2024-07-05 11:51:52,011 WARN  [org.apache.activemq.artemis.core.client] AMQ212037: Connection failure to /127.0.0.1:60026 has been detected: AMQ229014: Did not receive data from /127.0.0.1:60026 within the 1,000ms connection TTL. The connection will now be closed. [code=CONNECTION_TIMEDOUT]
```

At at the Listener:

```
Consuming message.. 
Unexpected exception : 
javax.jms.IllegalStateException: The Consumer is closed
	at org.apache.activemq.ActiveMQMessageConsumer.checkClosed(ActiveMQMessageConsumer.java:879)
	at org.apache.activemq.ActiveMQMessageConsumer.receive(ActiveMQMessageConsumer.java:566)
	at example.Listener.main(Listener.java:61)
```

This seems to indicate the reconnect() (after broker has timed out the connection) is not connecting to the broker.
Expected behavior would be for the Listener to reconnect. 
