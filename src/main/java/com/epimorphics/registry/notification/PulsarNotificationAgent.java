package com.epimorphics.registry.notification;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.ClientBuilderImpl;
import org.apache.pulsar.client.impl.conf.ClientConfigurationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PulsarNotificationAgent implements NotificationAgent {
    private final Logger log = LoggerFactory.getLogger(PulsarNotificationAgent.class);
    private PulsarClient client;

    public void setClient(PulsarClient client) {
        this.client = client;
    }

    public void setConfig(ClientConfigurationData config) {
        try {
            this.client = new ClientBuilderImpl(config).build();
            log.info("Pulsar notification client configured successfully.");
        } catch (Exception e) {
            log.error("Failed to build Pulsar client from config.", e);
        }
    }

    @Override public void send(Notification notification) throws Exception {
        String msg = notification.getMessage();
        String target = notification.getTarget();
        String operation = notification.getOperation();
        List<String> topics = notification.getTopics();

        if (client == null) {
            String error = "Unable to send notification for " + target + ": Pulsar client not configured.";
            throw new IllegalStateException(error);
        }

        for (String topic : topics) {
            try (Producer<String> producer = client.newProducer(Schema.STRING).topic(topic).create()) {
                log.debug("Sending Pulsar notification to topic:" + topic + " for target: " + target + ", operation: " + operation + ", message: " + msg);
                producer.newMessage()
                        .property("target", target)
                        .property("operation", operation)
                        .value(msg)
                        .send();
            } catch (Exception e) {
                log.error("Failed to send Pulsar notification for target: " + target, e);
                throw e;
            }
        }
    }
}
