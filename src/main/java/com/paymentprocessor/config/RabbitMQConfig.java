package com.paymentprocessor.config;

import com.paymentprocessor.service.CountryLoggingService;
import com.paymentprocessor.service.NotificationSendingService;
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
    public TopicExchange exchange() {
        return new TopicExchange("paymentprocessor-exchange");
    }

    @Bean
    public Queue notificationDeadLetterQueue() {
        return new Queue("notificationDeadLetterQueue", true, false, false);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue("notificationQueue", true, false, false,
                Map.of(
                        "x-dead-letter-exchange", "",
                        "x-dead-letter-routing-key", "notificationDeadLetterQueue"
                ));
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(exchange)
                .with("notificationRoutingKey");
    }

    @Bean
    public MessageListenerAdapter notificationListenerAdapter(NotificationSendingService notificationSendingService) {
        return new MessageListenerAdapter(notificationSendingService);
    }

    @Bean
    public SimpleMessageListenerContainer notificationContainer(ConnectionFactory connectionFactory,
                                                                MessageListenerAdapter notificationListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setDefaultRequeueRejected(false);
        container.setErrorHandler(throwable -> log.error("Listener failed - message rerouted to notificationDeadLetterQueue", throwable));
        container.setQueueNames("notificationQueue");
        container.setMessageListener(notificationListenerAdapter);
        return container;
    }

    @Bean
    public Queue countryLoggingDeadLetterQueue() {
        return new Queue("countryLoggingDeadLetterQueue", true, false, false);
    }

    @Bean
    public Queue countryLoggingQueue() {
        return new Queue("countryLoggingQueue", true, false, false,
                Map.of(
                        "x-dead-letter-exchange", "",
                        "x-dead-letter-routing-key", "countryLoggingDeadLetterQueue"
                ));
    }

    @Bean
    public Binding countryLoggingBinding(Queue countryLoggingQueue, TopicExchange exchange) {
        return BindingBuilder.bind(countryLoggingQueue)
                .to(exchange)
                .with("countryLoggingRoutingKey");
    }

    @Bean
    public MessageListenerAdapter countryLoggingListenerAdapter(CountryLoggingService countryLoggingService) {
        return new MessageListenerAdapter(countryLoggingService);
    }

    @Bean
    public SimpleMessageListenerContainer countryLoggingContainer(ConnectionFactory connectionFactory,
                                                                  MessageListenerAdapter countryLoggingListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setDefaultRequeueRejected(false);
        container.setErrorHandler(throwable -> log.error("Listener failed - message rerouted to countryLoggingDeadLetterQueue", throwable));
        container.setQueueNames("countryLoggingQueue");
        container.setMessageListener(countryLoggingListenerAdapter);
        return container;
    }

}
