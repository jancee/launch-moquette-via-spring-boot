package io.moquette;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.ClasspathResourceLoader;
import io.moquette.server.config.IConfig;
import io.moquette.server.config.IResourceLoader;
import io.moquette.server.config.ResourceLoaderConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.moquette.spi.impl.Utils.readBytesAndRewind;

@Component
public class EmbeddedLauncher {

    private class PublisherListener extends AbstractInterceptHandler {

        @Override
        public String getID() {
            return "EmbeddedLauncherPublishListener";
        }

        @Override
        public void onPublish(InterceptPublishMessage msg) {
            ByteBuf payload = msg.getPayload();
            byte[] payloadContent = readBytesAndRewind(payload);

            String stringPayload = new String(payloadContent);
//            Map<String, Object> mapPayload = (Map<String, Object>) JSONObject.parse(stringPayload);

            System.out.println("Received on topic: " + msg.getTopicName() + " content: " + stringPayload);
        }
    }

    public void initBroker() throws InterruptedException, IOException {
        IResourceLoader classpathLoader = new ClasspathResourceLoader();
        final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);

        final Server mqttBroker = new Server();
        List<? extends InterceptHandler> userHandlers = Collections.singletonList(new PublisherListener()); // pipeline拦截器
        mqttBroker.startServer(classPathConfig, userHandlers);

        System.out.println("Broker started press [CTRL+C] to stop");

        // 绑定安全关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping broker");
            mqttBroker.stopServer();
            System.out.println("Broker stopped");
        }));

        Thread.sleep(20000);
        System.out.println("Before self publish");
        MqttPublishMessage message = MqttMessageBuilders.publish()
            .topicName("/exit")
            .retained(true)
//        qos(MqttQoS.AT_MOST_ONCE);
//        qQos(MqttQoS.AT_LEAST_ONCE);
            .qos(MqttQoS.EXACTLY_ONCE)
            .payload(Unpooled.copiedBuffer("Hello World!!".getBytes()))
            .build();

        mqttBroker.internalPublish(message, "INTRLPUB");
        System.out.println("After self publish");
    }

    private EmbeddedLauncher() {
    }
}
