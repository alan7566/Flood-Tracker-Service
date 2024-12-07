package com.escamilla.sensor.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageHandler;


@Configuration
public class MqttConfig {
    MqttSSLUtil mqttSSLUtil = new MqttSSLUtil();
    @Value("${mqtt.clientId}")
    static String clientId;

    @Value("${mqtt.broker.fqdn}")
    static String brokerFqdn;

    @Value("${mqtt.broker.port}")
    static Integer brokerPort;


    private final String BROKER_URL = "ssl://"+brokerFqdn+":"+brokerPort; // Cambia al endpoint de tu broker
    private final String CLIENT_ID = clientId;
    String CA_CERT_PATH = "sensor/src/main/resources/certs/ca.crt";
    String CLIENT_CERT_PATH = "sensor/src/main/resources/certs/client.crt";
    String CLIENT_KEY_PATH = "sensor/src/main/resources/certs/client.key";

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{BROKER_URL});
        options.setSocketFactory(mqttSSLUtil.getSslSocketFactory(CA_CERT_PATH, CLIENT_CERT_PATH, CLIENT_KEY_PATH));
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }



    @Bean
    public AbstractMessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound(MqttPahoClientFactory mqttClientFactory) {
        String[] topics = {"sensors/#"}; // Suscripción a todos los tópicos bajo "sensors/"
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(CLIENT_ID, mqttClientFactory, topics);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1); // Nivel de calidad de servicio (QoS)
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }


    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        return message -> {
            // Este bean procesa el mensaje recibido del broker MQTT
            String payload = (String) message.getPayload();
            String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
            System.out.println("Mensaje recibido en topic [" + topic + "]: " + payload);
        };
    }
}
