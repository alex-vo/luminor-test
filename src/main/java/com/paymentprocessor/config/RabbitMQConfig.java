package com.paymentprocessor.config;

import com.paymentprocessor.service.NotificationReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Slf4j
public class RabbitMQConfig {

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("deadLetterQueue", true, false, false);
    }

    @Bean
    public Queue queue() {
        return new Queue("notificationQueue", true, false, false,
                Map.of(
                        "x-dead-letter-exchange", "",
                        "x-dead-letter-routing-key", "deadLetterQueue"
                ));
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("paymentprocessor-exchange");
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("");
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDefaultRequeueRejected(false);
        container.setErrorHandler(throwable -> log.error("Listener failed - message rerouted to deadLetterQueue", throwable));
        container.setQueueNames("notificationQueue");
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(NotificationReceiver notificationReceiver) {
        return new MessageListenerAdapter(notificationReceiver);
    }

}
