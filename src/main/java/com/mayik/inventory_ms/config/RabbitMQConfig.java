package com.mayik.inventory_ms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public Queue inventoryReserveQueue() { return new Queue("inventory-reserve-queue"); }
    @Bean
    public Queue inventoryReleaseQueue() { return new Queue("inventory-release-queue"); }

    @Bean
    public TopicExchange inventoryExchange() { return new TopicExchange("inventory-exchange"); }
    @Bean
    public TopicExchange orderCompensationExchange() { return new TopicExchange("order-compensation-exchange"); }

    @Bean
    public Binding bindingInventoryReserve(Queue inventoryReserveQueue, TopicExchange inventoryExchange) {
        return BindingBuilder.bind(inventoryReserveQueue).to(inventoryExchange).with("inventory.reserve");
    }
    @Bean
    public Binding bindingInventoryRelease(Queue inventoryReleaseQueue, TopicExchange orderCompensationExchange) {
        return BindingBuilder.bind(inventoryReleaseQueue).to(orderCompensationExchange).with("inventory.release");
    }
}
