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
package org.apache.cxf.systest.handlers;


import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.common.util.PackageUtils;
import org.apache.handler_test.PingException;
import org.apache.handler_test.types.Ping;
import org.apache.handler_test.types.PingResponse;
import org.apache.handler_test.types.PingWithArgs;
import org.apache.hello_world_soap_http.types.GreetMe;


public class TestHandler<T extends LogicalMessageContext> 
    extends TestHandlerBase implements LogicalHandler<T> {

    private final JAXBContext jaxbCtx;

    public TestHandler() {
        this(true);
    } 

    public TestHandler(boolean serverSide) {
        super(serverSide); 

        try {
            jaxbCtx = JAXBContext.newInstance(PackageUtils.getPackageName(GreetMe.class));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
 
    } 

    public String getHandlerId() { 
        return "handler" + getId();
    } 

    public boolean handleMessage(T ctx) {
        methodCalled("handleMessage");
        printHandlerInfo("handleMessage", isOutbound(ctx));

        boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        boolean ret = handleMessageRet; 

        if (!isServerSideHandler()) {
            return true;
        }

        try {
            verifyJAXWSProperties(ctx);
        } catch (PingException e) {
            e.printStackTrace();
            throw new ProtocolException(e);
        }
        
        QName operationName = (QName)ctx.get(MessageContext.WSDL_OPERATION);
        assert operationName != null : "unable to get operation name from " + ctx;

        if ("ping".equals(operationName.getLocalPart())) {
            ret = handlePingMessage(outbound, ctx);
        } else if ("pingWithArgs".equals(operationName.getLocalPart())) {
            ret = handlePingWithArgsMessage(outbound, ctx); 
        }
        return ret;
    }


    private boolean handlePingWithArgsMessage(boolean outbound, T ctx) { 

        
        LogicalMessage msg = ctx.getMessage();
        Object payload = msg.getPayload(jaxbCtx); 
        addHandlerId(ctx.getMessage(), ctx, outbound);

        boolean ret = true;
        if (payload instanceof PingWithArgs) {
            String arg = ((PingWithArgs)payload).getHandlersCommand();
            
            StringTokenizer strtok = new StringTokenizer(arg, " ");
            String hid = strtok.nextToken();
            String direction = strtok.nextToken();
            String command = strtok.nextToken();
            
            if (outbound) {
                return ret;
            }

            if (getHandlerId().equals(hid)
                && "inbound".equals(direction)) {
                
                if ("stop".equals(command)) {
                    PingResponse resp = new PingResponse();
                    getHandlerInfoList(ctx).add(getHandlerId()); 
                    resp.getHandlersInfo().addAll(getHandlerInfoList(ctx));
                    msg.setPayload(resp, jaxbCtx);
                    ret = false;
                } else if ("throw".equals(command)) {
                    throwException(strtok.nextToken());
                }
            }
        }
        return ret;
    } 


    private void throwException(String exType) { 
        if (exType.contains("ProtocolException")) {
            throw new ProtocolException("from server handler");
        }
    } 

    private boolean handlePingMessage(boolean outbound, T ctx) { 

        LogicalMessage msg = ctx.getMessage();
        addHandlerId(msg, ctx, outbound);
        return handleMessageRet;
    } 

    private void addHandlerId(LogicalMessage msg, T ctx, boolean outbound) { 

        Object obj = msg.getPayload(jaxbCtx);
        if (obj instanceof PingResponse) {
            PingResponse origResp = (PingResponse)obj;
            PingResponse newResp = new PingResponse();
            newResp.getHandlersInfo().addAll(origResp.getHandlersInfo());
            newResp.getHandlersInfo().add(getHandlerId());
            msg.setPayload(newResp, jaxbCtx);
        } else if (obj instanceof Ping) {
            getHandlerInfoList(ctx).add(getHandlerId());
        }
    } 


    public boolean handleFault(T ctx) {
        methodCalled("handleFault");
        printHandlerInfo("handleFault", isOutbound(ctx));
        //boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        return true;
    }

    public void close(MessageContext arg0) {
        methodCalled("close");
    }

    public void init(Map arg0) {
        methodCalled("init");
    }

    public void destroy() {
        methodCalled("destroy");
    }

    public String toString() { 
        return getHandlerId(); 
    } 
}    
