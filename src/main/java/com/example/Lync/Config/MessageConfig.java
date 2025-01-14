package com.example.Lync.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {

    // Exchange and routing keys
    public static final String EXCHANGE = "notification.exchange";
    public static final String BUYER_ROUTING_KEY = "notifications.buyer";
    public static final String SELLER_ROUTING_KEY = "notifications.seller";
    public static final String ADMIN_ROUTING_KEY = "notifications.admin";
    public static final String DEFAULT_ROUTING_KEY = "notifications.general";

    // Queue names
    public static final String BUYER_QUEUE = "buyer.notifications";
    public static final String SELLER_QUEUE = "seller.notifications";
    public static final String ADMIN_QUEUE = "admin.notifications";
    public static final String DEFAULT_QUEUE = "general.notifications";

    // Define queues
    @Bean
    public Queue buyerQueue() {
        return new Queue(BUYER_QUEUE, true);
    }

    @Bean
    public Queue sellerQueue() {
        return new Queue(SELLER_QUEUE, true);
    }

    @Bean
    public Queue adminQueue() {
        return new Queue(ADMIN_QUEUE, true);
    }

    @Bean
    public Queue defaultQueue() {
        return new Queue(DEFAULT_QUEUE, true);
    }

    // Define the exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // Bind queues to exchange with routing keys
    @Bean
    public Binding buyerBinding(Queue buyerQueue, TopicExchange exchange) {
        return BindingBuilder.bind(buyerQueue).to(exchange).with(BUYER_ROUTING_KEY);
    }

    @Bean
    public Binding sellerBinding(Queue sellerQueue, TopicExchange exchange) {
        return BindingBuilder.bind(sellerQueue).to(exchange).with(SELLER_ROUTING_KEY);
    }

    @Bean
    public Binding adminBinding(Queue adminQueue, TopicExchange exchange) {
        return BindingBuilder.bind(adminQueue).to(exchange).with(ADMIN_ROUTING_KEY);
    }

    @Bean
    public Binding defaultBinding(Queue defaultQueue, TopicExchange exchange) {
        return BindingBuilder.bind(defaultQueue).to(exchange).with(DEFAULT_ROUTING_KEY);
    }

    // Define the message converter
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Define the RabbitTemplate
    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
