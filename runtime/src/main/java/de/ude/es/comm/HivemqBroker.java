package de.ude.es.comm;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;

import java.nio.ByteBuffer;

public class HivemqBroker implements CommunicationEndpoint {

    private Mqtt5AsyncClient client;
    private final String identifier;

    public HivemqBroker(String identifier) {
        this.identifier = identifier;

        Mqtt5BlockingClient blockingClient =
                MqttClient.builder()
                        .useMqttVersion5()
                        .identifier(identifier)
                        .serverHost("localhost")
                        .serverPort(1883)
                        //  .automaticReconnectWithDefaultConfig()
                        .buildBlocking();
        Mqtt5ConnAck connAck = blockingClient.connect();
        client = blockingClient.toAsync();
    }

    public HivemqBroker(String identifier, int port) {
        this.identifier = identifier;

        Mqtt5BlockingClient blockingClient =
                MqttClient.builder()
                        .useMqttVersion5()
                        .identifier(identifier)
                        .serverHost("localhost")
                        .serverPort(port)
                        //  .automaticReconnectWithDefaultConfig()
                        .buildBlocking();
        Mqtt5ConnAck connAck = blockingClient.connect();
        client = blockingClient.toAsync();
    }

    @Override
    public void publish(Posting posting) {
        client.publishWith()
                .topic(posting.cloneWithTopicAffix(identifier).topic())
                .payload(posting.data().getBytes())
                .qos(MqttQos.EXACTLY_ONCE).send()
                .whenComplete((pubAck, throwable) -> onPublishComplete(pubAck, throwable));

    }

    private void onPublishComplete(Mqtt5PublishResult pubAck, Throwable throwable) {
        if (throwable != null) {
            System.out.println("publish failed");
        } else {
            System.out.println("Successfully published to: " + pubAck.getPublish().getTopic());
        }
    }

    @Override
    public void subscribe(String topic, Subscriber subscriber) {
        subscribeRaw(identifier + topic, subscriber);
    }

    @Override
    public void unsubscribe(String topic, Subscriber subscriber) {
        unsubscribeRaw(identifier + topic, subscriber);
    }

    @Override
    public void subscribeRaw(String topic, Subscriber subscriber) {
        client.subscribeWith()
                .topicFilter(topic)
                .callback(publish -> subscriber.deliver(new Posting(topic, unwrapPayload(publish.getPayload().get()))))
                .send().whenComplete(((subAck, throwable) -> onSubscribeComplete(throwable, topic)));
    }

    private void onSubscribeComplete(Throwable subFailed, String topic) {
        if (subFailed != null) {
            System.out.println("sub failed: " + topic);
        } else {
            System.out.println("Subscribed to: " + topic);
        }
    }

    private static String unwrapPayload(ByteBuffer payload) {
        byte[] realpayload = new byte[payload.remaining()];
        payload.get(realpayload);
        return new String(realpayload);
    }

    @Override
    public void unsubscribeRaw(String topic, Subscriber subscriber) {
        client.unsubscribeWith().topicFilter(topic).send().whenComplete(((unsubAck, throwable) -> onUnsubscribeComplete(throwable, topic)));
    }

    public void onUnsubscribeComplete(Throwable throwable, String topic) {
        if (throwable != null) {
            System.out.println("unsub failed for: " + topic);
        } else {
            System.out.println("unsubscribe from: " + topic);
        }
    }

    @Override
    public String ID() {
        return identifier;
    }

    public void closeConnection() {
        client.disconnect();
    }
}
