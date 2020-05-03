package com.paymentprocessor.config;

import com.paymentprocessor.service.CountryLoggingService;
import com.paymentprocessor.service.NotificationSendingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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
    public DirectExchange exchange() {
        return new DirectExchange("paymentprocessor-exchange");
    }

    @Bean
    public Queue notificationDeadLetterQueue() {
        return new Queue(RabbitMQSettings.NOTIFICATION_DEAD_LETTER_QUEUE_NAME, true, false, false);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue("notificationQueue", true, false, false,
                Map.of(
                        "x-dead-letter-exchange", "",
                        "x-dead-letter-routing-key", RabbitMQSettings.NOTIFICATION_DEAD_LETTER_QUEUE_NAME
                ));
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange exchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(exchange)
                .with(RabbitMQSettings.NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public MessageListenerAdapter notificationListenerAdapter(NotificationSendingService notificationSendingService) {
        return new MessageListenerAdapter(notificationSendingService);
    }

    @Bean
    public SimpleMessageListenerContainer notificationContainer(ConnectionFactory connectionFactory,
                                                                MessageListenerAdapter notificationListenerAdapter,
                                                                Queue notificationQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setDefaultRequeueRejected(false);
        container.setErrorHandler(throwable -> log.error("Listener failed - message rerouted to " + RabbitMQSettings.NOTIFICATION_DEAD_LETTER_QUEUE_NAME, throwable));
        container.setQueues(notificationQueue);
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
                        "x-dead-letter-routing-key", RabbitMQSettings.COUNTRY_LOGGING_DEAD_LETTER_QUEUE_NAME
                ));
    }

    @Bean
    public Binding countryLoggingBinding(Queue countryLoggingQueue, DirectExchange exchange) {
        return BindingBuilder.bind(countryLoggingQueue)
                .to(exchange)
                .with(RabbitMQSettings.COUNTRY_LOGGING_ROUTING_KEY);
    }

    @Bean
    public MessageListenerAdapter countryLoggingListenerAdapter(CountryLoggingService countryLoggingService) {
        return new MessageListenerAdapter(countryLoggingService);
    }

    @Bean
    public SimpleMessageListenerContainer countryLoggingContainer(ConnectionFactory connectionFactory,
                                                                  MessageListenerAdapter countryLoggingListenerAdapter,
                                                                  Queue countryLoggingQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setDefaultRequeueRejected(false);
        container.setErrorHandler(throwable -> log.error("Listener failed - message rerouted to countryLoggingDeadLetterQueue", throwable));
        container.setQueues(countryLoggingQueue);
        container.setMessageListener(countryLoggingListenerAdapter);
        return container;
    }

}
