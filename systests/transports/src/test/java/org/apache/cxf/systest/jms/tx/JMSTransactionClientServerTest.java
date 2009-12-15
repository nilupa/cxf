/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.systest.jms.tx;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.xml.namespace.QName;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.apache.cxf.testutil.common.EmbeddedJMSBrokerLauncher;
import org.apache.cxf.transport.jms.JMSConfigFeature;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.apache.hello_world_doc_lit.Greeter;
import org.apache.hello_world_doc_lit.PingMeFault;
import org.apache.hello_world_doc_lit.SOAPService2;
import org.junit.Before;
import org.junit.Test;

public class JMSTransactionClientServerTest extends AbstractBusClientServerTestBase {
    
    protected static boolean serversStarted;

    @Before
    public void startServers() throws Exception {
        if (serversStarted) {
            return;
        }
        Map<String, String> props = new HashMap<String, String>();                
        if (System.getProperty("activemq.store.dir") != null) {
            props.put("activemq.store.dir", System.getProperty("activemq.store.dir"));
        }
        props.put("java.util.logging.config.file", 
                  System.getProperty("java.util.logging.config.file"));
        
        assertTrue("server did not launch correctly", 
                   launchServer(EmbeddedJMSBrokerLauncher.class, props, null));

        assertTrue("server did not launch correctly", 
                   launchServer(Server.class, false));
        serversStarted = true;
    }
    
    public URL getWSDLURL(String s) throws Exception {
        return getClass().getResource(s);
    }
    public QName getServiceName(QName q) {
        return q;
    }
    public QName getPortName(QName q) {
        return q;
    }
    
    @Test
    public void testDocBasicConnection() throws Exception {
        QName serviceName = getServiceName(new QName("http://apache.org/hello_world_doc_lit", 
                                 "SOAPService2"));
        QName portName = getPortName(new QName("http://apache.org/hello_world_doc_lit", "SoapPort2"));
        URL wsdl = getWSDLURL("/wsdl/hello_world_doc_lit.wsdl");
        assertNotNull(wsdl);

        SOAPService2 service = new SOAPService2(wsdl, serviceName);
        assertNotNull(service);

        Greeter greeter = service.getPort(portName, Greeter.class);
        doService(greeter, true);
    }
    @Test
    @org.junit.Ignore("does't work with the version of spring on 2.1.x")
    public void testNonAopTransaction() throws Exception {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(Greeter.class);
        factory.setAddress("jms://");

        JMSConfiguration jmsConfig = new JMSConfiguration();
        ConnectionFactory connectionFactory
            = new org.apache.activemq.ActiveMQConnectionFactory("tcp://localhost:61500");
        jmsConfig.setConnectionFactory(connectionFactory);
        jmsConfig.setTargetDestination("greeter.queue.noaop");
        jmsConfig.setPubSubDomain(false);
        jmsConfig.setUseJms11(true);

        JMSConfigFeature jmsConfigFeature = new JMSConfigFeature();
        jmsConfigFeature.setJmsConfig(jmsConfig);
        factory.getFeatures().add(jmsConfigFeature);

        Greeter greeter = (Greeter)factory.create();
        doService(greeter, false);
    }    
    public void doService(Greeter greeter, boolean doEx) throws Exception {

        String response1 = new String("Hello ");
        
        try {
                          
            String greeting = greeter.greetMe("Good guy");
            assertNotNull("No response received from service", greeting);
            String exResponse = response1 + "Good guy";
            assertEquals("Get unexcpeted result", exResponse, greeting);

            greeting = greeter.greetMe("Bad guy");
            assertNotNull("No response received from service", greeting);
            exResponse = response1 + "[Bad guy]";
            assertEquals("Get unexcpeted result", exResponse, greeting);
            
            if (doEx) {
                try {
                    greeter.pingMe();
                    fail("Should have thrown FaultException");
                } catch (PingMeFault ex) {
                    assertNotNull(ex.getFaultInfo());
                }
            }
        } catch (UndeclaredThrowableException ex) {
            throw (Exception)ex.getCause();
        }
    }

}
