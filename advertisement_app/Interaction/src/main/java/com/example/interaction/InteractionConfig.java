package com.example.interaction;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@ComponentScan("com.example.interaction")
@EnableJpaRepositories("com.example.interaction.domain.repo")
@EntityScan("com.example.interaction.domain.model")
public class InteractionConfig
{
    @Bean
    public CorsConfig dsad()
    {
        return new CorsConfig();
    }



    // JMS converter using the dedicated mapper
    @Bean
    @Qualifier("jmsMessageConverter")
    public MappingJackson2MessageConverter jmsMessageConverter(@Qualifier("jmsObjectMapper") ObjectMapper jmsObjectMapper) {
         MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(jmsObjectMapper);
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type"); // for @JmsListener type conversion
        return converter;
    }

    // JmsTemplate using the JMS converter
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory,
                                   @Qualifier("jmsMessageConverter") MappingJackson2MessageConverter converter) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(converter);
        template.setPubSubDomain(true); // if you’re using a topic
        return template;
    }

    // Listener factory using the JMS converter
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerFactory(
            ConnectionFactory connectionFactory,
            @Qualifier("jmsMessageConverter") MappingJackson2MessageConverter converter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setPubSubDomain(true); // if you’re using a topic
        return factory;
    }
}
