/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.jms.*;

// Added to call sleep 
import java.lang.Thread;  

class Listener {


    public static void main(String []args) throws JMSException {

        String user = env("ACTIVEMQ_USER", "admin");
        String password = env("ACTIVEMQ_PASSWORD", "password");
        String host = env("ACTIVEMQ_HOST", "localhost");
        int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
        String destination = arg(args, 0, "TEST.FOO");


        // Adding maxInactivityDuration to force a connection timeout 
        //ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port + "?useKeepAlive=false&wireFormat.maxInactivityDuration=1000&wireFormat.maxInactivityDurationInitalDelay=1000");
        String brokerURL="tcp://" + host + ":" + port + "?useKeepAlive=false&wireFormat.maxInactivityDuration=1000&wireFormat.maxInactivityDurationInitalDelay=1000";
        ActiveMQReconnector amqReconnector = new ActiveMQReconnector(brokerURL);


        amqReconnector.connect();

        // create destination 
        Destination dest = amqReconnector.getSession().createQueue(destination);

        // create consumer 
        MessageConsumer consumer = amqReconnector.getSession().createConsumer(dest);
        long start = System.currentTimeMillis();
        // remove need for count match 
        long count = 1;
        while(true) {
            System.out.println("Consuming message.. ");
            Message msg = null;
            try {
                msg = consumer.receive();
            } catch (Exception e) {
                System.out.println("Unexpected exception : ");
                System.out.println(ExceptionUtils.getStackTrace(e));
                amqReconnector.reconnect();
            }

            if( msg instanceof  TextMessage ) {
                String body = ((TextMessage) msg).getText();
                System.out.println(String.format("Received message body %s .", body));
                if( "SHUTDOWN".equals(body)) {
                    long diff = System.currentTimeMillis() - start;
                    System.out.println(String.format("Received %d in %.2f seconds", count, (1.0*diff/1000.0)));
                    break;
                } else {
                    //if( count != msg.getIntProperty("id") ) {
                    //    System.out.println("mismatch: "+count+"!="+msg.getIntProperty("id"));
                    //}
                    count = msg.getIntProperty("id");

                    //if( count == 0 ) {
                    //    start = System.currentTimeMillis();
                    //}
                    //if( count % 1000 == 0 ) {
                        System.out.println(String.format("Received %d messages.", count));
                    //}
                    //count ++;
                    // add a sleep  to force a connection timeout 
                    //longer enough than TTL (1000)
                    System.out.println("Add Sleep to force connection timeout.");
                    try {
                        Thread.sleep(4000);
                    } catch (Exception e) {
                        System.out.println("Unexpected exception : ");
                        System.out.println(ExceptionUtils.getStackTrace(e));
                    }

                    
                }

            } else {
                System.out.println("Unexpected message type: "+msg);
            }

        }
        amqReconnector.getConnection().close();
    }

    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if( rc== null )
            return defaultValue;
        return rc;
    }

    private static String arg(String []args, int index, String defaultValue) {
        if( index < args.length )
            return args[index];
        else
            return defaultValue;
    }

}