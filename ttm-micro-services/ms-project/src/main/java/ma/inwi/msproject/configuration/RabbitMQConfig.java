package ma.inwi.msproject.configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "ttm-queue";
    public static final String EXCHANGE = "ttm-exchange";
    public static final String ROUTING_KEY = "ttm-routing-key";

    public static final String AFFECTATION_GATES_QUEUE = "departments-gates-email-queue";
    public static final String AFFECTATION_GATES_ROUTING_KEY = "departments-gates-routing-key";

    public static final String NEXT_GATE_QUEUE = "next-gate-email-queue";
    public static final String NEXT_GATE_ROUTING_KEY = "next-gate-routing-key";

    public static final String IMPACT_ADDED_QUEUE = "impact-mail-queue";
    public static final String IMPACT_ADDED_ROUTING_KEY = "impact-mail-routing-key";

    public static final String RESPONSE_ADDED_QUEUE = "response-mail-queue";
    public static final String RESPONSE_ADDED_ROUTING_KEY = "response-mail-routing-key";

    public static final String PROJECT_AFFECTED_INTERLOCUTOR_RESPONSE_QUEUE = "project-affectation-interlocutor-response-queue";
    public static final String PROJECT_AFFECTED_INTERLOCUTOR_RESPONSE_ROUTING_KEY = "project-affectation-interlocutor-response-routing-key";

    public static final String AFFECTATION_IMPACT_QUEUE  = "affectation-impact-queue";
    public static final String AFFECTATION_IMPACT_ROUTING_KEY  = "affectation-impact-routing-key";

    public static final String IMPACT_MODIFICATION_QUEUE = "impact-modification-queue";
    public static final String IMPACT_MODIFICATION_ROUTING_KEY = "impact-modification-routing-key";

    public static final String GATE_SUSPENSION_QUEUE = "gate-suspension-queue";
    public static final String GATE_SUSPENSION_ROUTING_KEY = "gate-suspension-routing-key";




    // Define the queue
    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    // Define second queue
    @Bean
    public Queue affectationGatesQueue() {
        return QueueBuilder.durable(AFFECTATION_GATES_QUEUE).build();
    }

    @Bean
    public Queue nextGateQueue() {
        return QueueBuilder.durable(NEXT_GATE_QUEUE).build();
    }

    @Bean
    public Queue addImpactQueue() {
        return QueueBuilder.durable(IMPACT_ADDED_QUEUE).build();
    }

    @Bean
    public Queue addResponseQueue() {
        return QueueBuilder.durable(RESPONSE_ADDED_QUEUE).build();
    }

    @Bean
    public Queue affectationImpactSignalingInterlocutorQueue() {
        return QueueBuilder.durable(AFFECTATION_IMPACT_QUEUE).build();
    }

    @Bean
    public Queue affectationImpactRespondingInterlocutorQueue() {
        return QueueBuilder.durable(PROJECT_AFFECTED_INTERLOCUTOR_RESPONSE_QUEUE).build();
    }

    @Bean
    public Queue affectationImpactModifiedQueue() {
        return QueueBuilder.durable(IMPACT_MODIFICATION_QUEUE).build();
    }

    @Bean
    public Queue gateSuspensionQueue() {
        return QueueBuilder.durable(GATE_SUSPENSION_QUEUE).build();
    }




    // Define the exchange
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    // Bind the queue to the exchange with the routing key
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // Bind second queue with its routing key
    @Bean
    public Binding bindingAffectationQueue(Queue affectationGatesQueue, DirectExchange exchange) {
        return BindingBuilder.bind(affectationGatesQueue).to(exchange).with(AFFECTATION_GATES_ROUTING_KEY);
    }

    @Bean
    public Binding bindingNextGateQueue(Queue nextGateQueue, DirectExchange exchange) {
        return BindingBuilder.bind(nextGateQueue).to(exchange).with(NEXT_GATE_ROUTING_KEY);
    }

    @Bean
    public Binding bindingImpactAddQueue(Queue addImpactQueue, DirectExchange exchange) {
        return BindingBuilder.bind(addImpactQueue).to(exchange).with(IMPACT_ADDED_ROUTING_KEY);
    }

    @Bean
    public Binding bindingResponseAddQueue(Queue addResponseQueue, DirectExchange exchange) {
        return BindingBuilder.bind(addResponseQueue).to(exchange).with(RESPONSE_ADDED_ROUTING_KEY);
    }


    @Bean
    public Binding bindingAffectationSignalingImpactQueue(Queue affectationImpactSignalingInterlocutorQueue, DirectExchange exchange) {
        return BindingBuilder.bind(affectationImpactSignalingInterlocutorQueue).to(exchange).with(AFFECTATION_IMPACT_ROUTING_KEY);
        /*
    public Binding bindingAffectationImpactQueue(Queue affectationImpactQueue, DirectExchange exchange) {
        return BindingBuilder.bind(affectationImpactQueue).to(exchange).with(AFFECTATION_IMPACT_ROUTING_KEY);
>>>>>>> c098898c7e3ad205b75a8ef86b565b4175d2db4e
    }

     */
    }


    @Bean
    public Binding bindingAffectationRespondingImpactQueue(Queue addResponseQueue, DirectExchange exchange) {
        return BindingBuilder.bind(addResponseQueue).to(exchange).with(PROJECT_AFFECTED_INTERLOCUTOR_RESPONSE_ROUTING_KEY);
    }


    @Bean
    public Binding bindingImpactModificationQueue(Queue affectationImpactModifiedQueue, DirectExchange exchange) {
        return BindingBuilder.bind(affectationImpactModifiedQueue).to(exchange).with(PROJECT_AFFECTED_INTERLOCUTOR_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Binding bindingGateSuspensionQueue(Queue gateSuspensionQueue, DirectExchange exchange) {
        return BindingBuilder.bind(gateSuspensionQueue).to(exchange).with(PROJECT_AFFECTED_INTERLOCUTOR_RESPONSE_ROUTING_KEY);
    }




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


}
