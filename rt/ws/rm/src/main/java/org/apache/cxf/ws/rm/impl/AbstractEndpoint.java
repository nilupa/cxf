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

package org.apache.cxf.ws.rm.impl;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.ws.rm.Identifier;

public class AbstractEndpoint {
    
    private final RMEndpoint reliableEndpoint;
    
    protected AbstractEndpoint(RMEndpoint rme) {
        reliableEndpoint = rme;
    }
    
    public QName getName() {
        return reliableEndpoint.getName();
    }
    
    /** 
     * @return Returns the reliableEndpoint.
     */
    public RMEndpoint getReliableEndpoint() {
        return reliableEndpoint;
    }

    /**
     * @return Returns the interceptor.
     */
    public RMInterceptor getInterceptor() {
        return reliableEndpoint.getInterceptor();
    }
    
    /**
     * @return Returns the endpoint.
     */
    public Endpoint getEndpoint() {
        return reliableEndpoint.getEndpoint();
    }
    
    /**
     * @return Returns the proxy.
     */
    public Proxy getProxy() {
        return reliableEndpoint.getProxy();
    }
    
    /**
     * @return Returns the servant.
     */
    public Servant getServant() {
        return reliableEndpoint.getServant();
    }

    /**
     * Generates and returns a new sequence identifier.
     * 
     * @return the sequence identifier.
     */
    public Identifier generateSequenceIdentifier() {
        String sequenceID = ContextUtils.generateUUID();
        Identifier sid = RMUtils.getWSRMFactory().createIdentifier();
        sid.setValue(sequenceID);        
        return sid;
    }
}
