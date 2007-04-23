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

package org.apache.cxf.interceptor;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.MessageObserver;

public abstract class AbstractFaultChainInitiatorObserver implements MessageObserver {
    
    private static final Logger LOG = Logger.getLogger(AbstractFaultChainInitiatorObserver.class.getName());
    
    private Bus bus;

    public AbstractFaultChainInitiatorObserver(Bus bus) {
        this.bus = bus;
    }

    public void onMessage(Message message) {
      
        assert  null != message;
        Exchange exchange = message.getExchange();

        Message faultMessage = null;

        // now that we have switched over to the fault chain,
        // prevent any further operations on the in/out message 

        if (isOutboundObserver()) {
            Exception ex = message.getContent(Exception.class);
            if (!(ex instanceof Fault)) {
                ex = new Fault(ex);
            }
            
            faultMessage = exchange.getOutMessage();
            if (null == faultMessage) {
                faultMessage = exchange.get(Endpoint.class).getBinding().createMessage();
            }
            faultMessage.setContent(Exception.class, ex);
            assert exchange.get(Exception.class) == ex;
            exchange.setOutMessage(null);
            exchange.setOutFaultMessage(faultMessage);
        } else {
            faultMessage = message;
            exchange.setInMessage(null);
            exchange.setInFaultMessage(faultMessage);
        }          
         
       
        // setup chain
        PhaseInterceptorChain chain = new PhaseInterceptorChain(getPhases());
        initializeInterceptors(faultMessage.getExchange(), chain);
        
        faultMessage.setInterceptorChain(chain);
        try {
            chain.doIntercept(faultMessage);
        } catch (Exception exc) {
            LogUtils.log(LOG, Level.INFO, "Error occured during error handling, give up!", exc);
        }
    }

    protected abstract boolean isOutboundObserver();

    protected abstract List<Phase> getPhases();

    protected void initializeInterceptors(Exchange ex, PhaseInterceptorChain chain) {
        
    }

    public Bus getBus() {
        return bus;
    }

}
