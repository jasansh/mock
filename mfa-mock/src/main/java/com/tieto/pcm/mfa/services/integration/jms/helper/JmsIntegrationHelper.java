package com.tieto.pcm.mfa.services.integration.jms.helper;

import static java.lang.String.format;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.jms.JmsConstants;
import com.ibm.msg.client.wmq.common.CommonConstants;

public class JmsIntegrationHelper {

    protected static final Logger logger = LoggerFactory.getLogger(JmsIntegrationHelper.class);

    protected Environment env;

    protected GenericApplicationContext applicationContext;

    protected Set<String> defaultMessageListenerContainerBeanNames = Collections.synchronizedSet(new HashSet<>());

    public JmsIntegrationHelper(Environment env, GenericApplicationContext applicationContext) {

        this.env = env;
        this.applicationContext = applicationContext;
    }

    public JmsTemplate getOrCreateJmsTemplate(String moduleName, String direction) throws JMSException {

        String beanName = moduleName + "_" + direction + "_" + JmsTemplate.class.getSimpleName();
        if (applicationContext.containsBean(beanName)) {
            return (JmsTemplate) applicationContext.getBean(beanName);
        } else {
            JmsTemplate jmsTemplate = createJmsTemplate(moduleName, direction);
            applicationContext.registerBean(beanName, JmsTemplate.class, () -> jmsTemplate);
            jmsTemplate.afterPropertiesSet();
            logger.debug("Created bean with name: {}", beanName);
            return jmsTemplate;
        }
    }

    public DefaultMessageListenerContainer getOrCreateMessageListenerContainer(String moduleName, String direction) throws JMSException {

        String beanName = moduleName + "_" + direction + "_" + DefaultMessageListenerContainer.class.getSimpleName();
        if (applicationContext.containsBean(beanName)) {
            return (DefaultMessageListenerContainer) applicationContext.getBean(beanName);
        } else {
            DefaultMessageListenerContainer messageListenerContainer = createMessageListenerContainer(moduleName, direction);
            applicationContext.registerBean(beanName, DefaultMessageListenerContainer.class, () -> messageListenerContainer);
            defaultMessageListenerContainerBeanNames.add(beanName);
            messageListenerContainer.afterPropertiesSet();
            logger.debug("Created bean with name: {}", beanName);
            messageListenerContainer.start();
            return messageListenerContainer;
        }
    }

    public void stopAndRemoveListeners() {

        defaultMessageListenerContainerBeanNames.forEach(beanName -> {

            if (applicationContext.containsBean(beanName)) {
                DefaultMessageListenerContainer container = (DefaultMessageListenerContainer) applicationContext.getBean(beanName);
                container.setReceiveTimeout(10);
                container.setMessageListener(null);
                container.stop();
                applicationContext.removeBeanDefinition(beanName);
            }
        });
    }

    protected JmsTemplate createJmsTemplate(String moduleName, String direction) throws JMSException {

        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(createConnectionFactory(moduleName, direction));
        template.setSessionTransacted(true);
        String jndiQueueName = env.getProperty(getJndiQueueName(moduleName, direction));
        logger.info("JmsTemplate using jndiQueueName: {}", jndiQueueName);
        try {
            logger.info("Performing JNDI lookup for queue: {} ", jndiQueueName);
            template.setDefaultDestination((Destination) new InitialContext().lookup(jndiQueueName));
            return template;
        } catch (Exception e) {
            logger.warn("JNDI lookup for queue {} failed ({})", jndiQueueName, e.getClass().getName());
        }

        String propertyName = getQueueName(moduleName, direction);
        String destinationName = env.getProperty(propertyName);
        logger.info("Using destination: {} (from property {})", destinationName, propertyName);
        template.setDefaultDestination(new ActiveMQQueue(destinationName));
        return template;
    }

    protected DefaultMessageListenerContainer createMessageListenerContainer(String moduleName, String direction) throws JMSException {

        String minPool = env.getProperty(format("jms.%s.%s.minPool", moduleName, direction), "1");
        String maxPool = env.getProperty(format("jms.%s.%s.maxPool", moduleName, direction), "2");

        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(createConnectionFactory(moduleName, direction));
        messageListenerContainer.setSessionTransacted(true);
        messageListenerContainer.setCacheLevel(0);
        messageListenerContainer.setConcurrency(minPool + "-" + maxPool);
        String jndiQueueName = env.getProperty(getJndiQueueName(moduleName, direction));
        logger.info("Lister using jndiQueueName: {}", jndiQueueName);
        try {
            logger.info("Performing JNDI lookup for queue: {} ", jndiQueueName);
            messageListenerContainer.setDestination((Queue) new InitialContext().lookup(jndiQueueName));
            return messageListenerContainer;
        } catch (Exception e) {
            logger.warn("JNDI lookup for queue {} failed ({})", jndiQueueName, e.getClass().getName());
        }

        String propertyName = getQueueName(moduleName, direction);
        String destinationName = env.getProperty(propertyName);
        logger.info("Using destination: {} (from property {})", destinationName, propertyName);
        messageListenerContainer.setDestination(new ActiveMQQueue(destinationName));
        return messageListenerContainer;
    }

    protected ConnectionFactory createConnectionFactory(String moduleName, String direction) throws JMSException {

        String jndiFactoryName = env.getProperty(getJndiFactoryName(moduleName, direction));
        logger.info("Using jndiFactoryName: {}", jndiFactoryName);
        try {
            logger.info("Performing JNDI lookup for connection factory: {} ", jndiFactoryName);
            return /*new CachingConnectionFactory(*/(ConnectionFactory) new InitialContext().lookup(jndiFactoryName);
        } catch (Exception e) {
            logger.warn("JNDI lookup for connection factory {} failed ({})", jndiFactoryName, e.getClass().getName());
        }

        String jmsBroker = env.getProperty(format("jms.%s.broker", moduleName));
        logger.info("Using jmsBroker: {})", jmsBroker);
        // Based on the value of this property we load the correct implementation for ConnectionFactory
        if ("ibmmq".equals(jmsBroker)) {
            logger.info("Using connection factory: {})", MQConnectionFactory.class.getName());
            MQConnectionFactory connectionFactory = new MQConnectionFactory();
            connectionFactory.setHostName(env.getProperty(format("jms.%s.ibmmq.hostName", moduleName)));
            connectionFactory.setPort(new Integer(env.getProperty(format("jms.%s.ibmmq.port", moduleName))));
            connectionFactory.setQueueManager(env.getProperty(format("jms.%s.ibmmq.qmName", moduleName)));
            connectionFactory.setChannel(env.getProperty(format("jms.%s.ibmmq.channel", moduleName)));
            connectionFactory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
            connectionFactory.setStringProperty(JmsConstants.USERID, env.getProperty(format("jms.%s.ibmmq.user", moduleName)));
            connectionFactory.setStringProperty(JmsConstants.PASSWORD, env.getProperty(format("jms.%s.ibmmq.password", moduleName)));
            return /*new CachingConnectionFactory(*/connectionFactory;
        } else {
            logger.info("Using connection factory: {})", ActiveMQConnectionFactory.class.getName());
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL(env.getProperty(format("jms.%s.activemq.broker.url", moduleName)));
            connectionFactory.setAlwaysSyncSend(true);
            return /*new PooledConnectionFactory(*/connectionFactory;
        }
    }

    protected String getJndiFactoryName(String moduleName, String direction) {
        return format("jms.%s.%s.jndiFactoryName", moduleName, direction);
    }

    protected String getJndiQueueName(String moduleName, String direction) {
        return format("jms.%s.%s.jndiQueueName", moduleName, direction);
    }

    protected String getQueueName(String moduleName, String direction) {
        return format("jms.%s.%s.queueName", moduleName, direction);
    }
}
