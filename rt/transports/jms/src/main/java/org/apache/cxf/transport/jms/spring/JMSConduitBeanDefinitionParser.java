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
package org.apache.cxf.transport.jms.spring;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import org.apache.cxf.configuration.spring.AbstractBeanDefinitionParser;
import org.apache.cxf.transport.jms.AddressType;
import org.apache.cxf.transport.jms.ClientBehaviorPolicyType;
import org.apache.cxf.transport.jms.ClientConfig;
import org.apache.cxf.transport.jms.JMSConduit;
import org.apache.cxf.transport.jms.SessionPoolType;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

public class JMSConduitBeanDefinitionParser extends AbstractBeanDefinitionParser {

    private static final String JMS_NS = "http://cxf.apache.org/transports/jms";

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        bean.setAbstract(true);
        mapElementToJaxbProperty(element, bean, new QName(JMS_NS, "clientConfig"), "clientConfig",
                                 ClientConfig.class);
        mapElementToJaxbProperty(element, bean, new QName(JMS_NS, "runtimePolicy"), "runtimePolicy", 
                                 ClientBehaviorPolicyType.class);
        mapElementToJaxbProperty(element, bean, new QName(JMS_NS, "address"), "JMSAddress", 
                                 AddressType.class);
        mapElementToJaxbProperty(element, bean, new QName(JMS_NS, "sessionPool"), "sessionPool", 
                                 SessionPoolType.class);
    }
    
    @Override
    protected String getJaxbPackage() {
        return "org.apache.cxf.transport.jms";
    }

    @Override
    protected Class getBeanClass(Element arg0) {
        return JMSConduit.class;
    }
}
