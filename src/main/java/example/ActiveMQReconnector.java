package example;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;


public class ActiveMQReconnector {

    //private static final String BROKER_URL = "tcp://localhost:61616";
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final long RECONNECT_DELAY = 5000; // 5 seconds

    private String brokerURL;
    private Connection connection;
    public Connection getConnection() {
        return connection;
    }

    private Session session;



    public Session getSession() {
        return session;
    }

    // Constructor
    public ActiveMQReconnector(String brokerURL) {
        this.brokerURL=brokerURL;
    }

    public void connect() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
        System.out.println("Attempting connect()");
        // Set up redelivery policy
        /* 
        RedeliveryPolicy policy = new RedeliveryPolicy();
        policy.setInitialRedeliveryDelay(1000);
        policy.setBackOffMultiplier(2);
        policy.setUseExponentialBackOff(true);
        policy.setMaximumRedeliveries(3);
        connectionFactory.setRedeliveryPolicy(policy);
        */
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        System.out.println("connect() attempt successful");

    }

    public void reconnect() {
        int attempts = 0;
        while (attempts < MAX_RECONNECT_ATTEMPTS) {
            try {
                if (connection != null) {
                    connection.close();
                }
                connect();
                System.out.println("Reconnected successfully");
                return;
            } catch (JMSException e) {
                attempts++;
                System.out.println("Reconnection attempt " + attempts + " failed. Retrying in " + RECONNECT_DELAY + "ms");
                try {
                    Thread.sleep(RECONNECT_DELAY);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println("Failed to reconnect after " + MAX_RECONNECT_ATTEMPTS + " attempts");
    }

    // Other methods for sending/receiving messages
}