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

package org.apache.cxf.jaxws.handler.soap;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.XMLMessage;

public class SOAPMessageContextImpl extends WrappedMessageContext implements SOAPMessageContext {
    private static final SAAJInInterceptor SAAJ_IN = new SAAJInInterceptor();
    
    private Set<String> roles = new HashSet<String>();
    
    public SOAPMessageContextImpl(Message m) {
        super(m, Scope.HANDLER);
        roles.add(getWrappedSoapMessage().getVersion().getNextRole());
    }

    public void setMessage(SOAPMessage message) {
        getWrappedMessage().setContent(SOAPMessage.class, message);
    }

    public SOAPMessage getMessage() {
        SOAPMessage message = getWrappedMessage().getContent(SOAPMessage.class);
        if (null == message) {
            Boolean outboundProperty = (Boolean)get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

            // No SOAPMessage exists yet, so lets create one
            if (!outboundProperty) {
                if (getWrappedMessage().getContent(Object.class) != null) {
                    //The Dispatch/Provider case:
                    Object obj = getWrappedMessage().getContent(Object.class);
                    if (obj instanceof SOAPMessage) {
                        message = (SOAPMessage)obj;
                    } else if (obj instanceof SOAPBody) {
                        // what to do
                    } else if (obj instanceof XMLMessage) {
                        // what to do
                    }
                } else {
                    SAAJ_IN.handleMessage(getWrappedSoapMessage());
                    message = getWrappedSoapMessage().getContent(SOAPMessage.class);
                }
            }
           
        }

        return message;
    }

    // TODO: handle the boolean parameter
    public Object[] getHeaders(QName name, JAXBContext context, boolean allRoles) {
        SOAPMessage msg = getMessage();
        SOAPHeader header;
        try {
            header = msg.getSOAPPart().getEnvelope().getHeader();
            if (header == null || !header.hasChildNodes()) {
                return new Object[0];
            }
            List<Object> ret = new ArrayList<Object>();
            Iterator<SOAPHeaderElement> it = CastUtils.cast(header.examineAllHeaderElements());
            while (it.hasNext()) {
                SOAPHeaderElement she = it.next();
                if ((allRoles
                    || roles.contains(she.getActor())) 
                    && name.equals(she.getElementQName())) {
                    
                    ret.add(context.createUnmarshaller().unmarshal(she));
                    
                }
            }
            return ret.toArray(new SOAPHeaderElement[ret.size()]);
        } catch (SOAPException e) {
            throw new WebServiceException(e);
        } catch (JAXBException e) {
            throw new WebServiceException(e);
        } 
    }

    public Set<String> getRoles() {
        return roles;
    }

    private SoapMessage getWrappedSoapMessage() {
        return (SoapMessage)getWrappedMessage();
    }
}
