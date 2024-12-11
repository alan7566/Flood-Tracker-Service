package com.escamilla.sensor.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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
    private String clientId;

    @Value("${mqtt.broker.fqdn}")
    private String brokerFqdn;

    @Value("${mqtt.broker.port}")
    private Integer brokerPort;


    private String getBrokerUrl() {
        return "ssl://" + brokerFqdn + ":" + brokerPort;
    }
    private final String CLIENT_ID = "FloodTrackerService";
    String CA_CERT_PATH = "sensor/src/main/resources/certs/ca.crt";
    String CLIENT_CERT_PATH = "sensor/src/main/resources/certs/client.crt";
    String CLIENT_KEY_PATH = "sensor/src/main/resources/certs/client.key";

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{getBrokerUrl()});
        options.setSocketFactory(mqttSSLUtil.getSslSocketFactory(CA_CERT_PATH, CLIENT_CERT_PATH, CLIENT_KEY_PATH));
        options.setCleanSession(true);
        options.setConnectionTimeout(60); // Set connection timeout to 30 seconds
        options.setKeepAliveInterval(120); // Set keep-alive interval to 60 seconds
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
        adapter.setCompletionTimeout(10000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1); // Nivel de calidad de servicio (QoS)
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }


}
