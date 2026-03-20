package ma.inwi.ms_notif.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;


@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "ttm-queue";
    public static final String AFFECTATION_GATES_QUEUE = "departments-gates-email-queue";
    public static final String NEXT_GATE_QUEUE = "next-gate-email-queue";
    public static final String IMPACT_ADDED_QUEUE = "impact-mail-queue";
    public static final String RESPONSE_ADDED_QUEUE = "response-mail-queue";
    public static final String PROJECT_AFFECTED_INTERLOCUTOR_RESPONSE_QUEUE = "project-affectation-interlocutor-response-queue";
    public static final String AFFECTATION_IMPACT_QUEUE  = "affectation-impact-queue";
    public static final String IMPACT_MODIFICATION_QUEUE = "impact-modification-queue";
    public static final String GATE_SUSPENSION_QUEUE = "gate-suspension-queue";



    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate getTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }


    // this used to prevent infinite retries
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setMessageConverter(messageConverter); // Ensure JSON conversion
        return factory;
    }






}
