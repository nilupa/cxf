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
package org.apache.cxf.transport.http;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusImpl;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.MessageObserver;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.cxf.message.Message.DECOUPLED_CHANNEL_MESSAGE;

/**
 */
public class HTTPConduitURLEasyMockTest extends Assert {

    private static final String NOWHERE = "http://nada.nothing.nowhere.null/";
    private static final String PAYLOAD = "message payload";
    private IMocksControl control;
    private EndpointInfo endpointInfo;
    private HttpURLConnectionFactory connectionFactory;
    private HttpURLConnection connection;
    private Proxy proxy;
    private Message inMessage;
    private MessageObserver observer;
    private ServletOutputStream os;
    private ServletInputStream is;
    
    /**
     * This is an extension to the HTTPConduit that replaces
     * the dynamic assignment of the HttpURLConnectionFactory,
     * and we just use the EasyMocked version for this test.
     */
    private class HTTPTestConduit extends HTTPConduit {
        HTTPTestConduit(
            Bus                      associatedBus, 
            EndpointInfo             endpoint, 
            EndpointReferenceType    epr,
            HttpURLConnectionFactory testFactory
        ) throws IOException {
            super(associatedBus, endpoint, epr);
            connectionFactory = testFactory;
        }
        @Override
        protected void retrieveConnectionFactory() {
            // do nothing. i.e do not change the connectionFactory field.
        }
    }
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        // avoid intermittent spurious failures on EasyMock detecting finalize
        // calls by mocking up only class data members (no local variables)
        // and explicitly making available for GC post-verify
        connectionFactory = null;
        connection = null;
        proxy = null;
        inMessage = null;
        observer = null;
        os = null;
        is = null;
    }


    @Test
    public void testSend() throws Exception {
        control = EasyMock.createNiceControl();
        HTTPConduit conduit = setUpConduit(true, false, false);
        Message message = new MessageImpl();
        conduit.prepare(message);
        verifySentMessage(conduit, message);
        finalVerify();
    }
    
    @Test
    public void testSendWithHeaders() throws Exception {
        control = EasyMock.createNiceControl();
        HTTPConduit conduit = setUpConduit(true, false, false);
        Message message = new MessageImpl();
        setUpHeaders(message);
        conduit.prepare(message);
        verifySentMessage(conduit, message, true);
        finalVerify();
    }
    
    @Test
    public void testSendHttpConnection() throws Exception {
        control = EasyMock.createNiceControl();
        HTTPConduit conduit = setUpConduit(true, false, false);
        Message message = new MessageImpl();
        conduit.prepare(message);
        verifySentMessage(conduit, message);
        finalVerify();
    }

    @Test
    public void testSendHttpConnectionAutoRedirect() throws Exception {
        control = EasyMock.createNiceControl();
        HTTPConduit conduit = setUpConduit(true, true, false);
        Message message = new MessageImpl();
        conduit.prepare(message);
        verifySentMessage(conduit, message);
        finalVerify();
    }
    
    @Test
    public void testSendDecoupled() throws Exception {
        control = EasyMock.createNiceControl();
        HTTPConduit conduit = setUpConduit(true, false, true);
        Message message = new MessageImpl();
        conduit.prepare(message);
        verifySentMessage(conduit, message, false, true);
        finalVerify();
    }
    
    
    private void setUpHeaders(Message message) {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentTypes = new ArrayList<String>();
        contentTypes.add("text/xml");
        contentTypes.add("charset=utf8");
        headers.put("content-type", contentTypes);
        message.put(Message.PROTOCOL_HEADERS, headers);
        
        AuthorizationPolicy authPolicy = new AuthorizationPolicy();
        authPolicy.setUserName("BJ");
        authPolicy.setPassword("value");
        message.put(AuthorizationPolicy.class, authPolicy);        
    }


    private HTTPConduit setUpConduit(
        boolean send,
        boolean autoRedirect,
        boolean decoupled
    ) throws Exception {
        endpointInfo = new EndpointInfo();
        endpointInfo.setAddress(NOWHERE + "bar/foo");
        connectionFactory = 
            control.createMock(HttpURLConnectionFactory.class);
        
        if (send) {
            //proxy = control.createMock(Proxy.class);
            proxy =  null;
            connection =
                control.createMock(HttpURLConnection.class);
            connectionFactory.createConnection(
                                      EasyMock.eq(proxy), 
                                      EasyMock.eq(new URL(NOWHERE + "bar/foo")));
            
            EasyMock.expectLastCall().andReturn(connection);
            connection.setDoOutput(true);
            EasyMock.expectLastCall();
            
            connection.setRequestMethod("POST");
            EasyMock.expectLastCall();
            
            if (!autoRedirect) {
                connection.getRequestMethod();
                EasyMock.expectLastCall().andReturn("POST");
                connection.setChunkedStreamingMode(-1);
                EasyMock.expectLastCall();                    
            }

            connection.setConnectTimeout(303030);
            EasyMock.expectLastCall();
            connection.setReadTimeout(404040);
            EasyMock.expectLastCall();
            connection.setUseCaches(false);
            EasyMock.expectLastCall();
            
        }

        CXFBusImpl bus = new CXFBusImpl();
        URL decoupledURL = null;
        if (decoupled) {
            decoupledURL = new URL(NOWHERE + "response");
            DestinationFactoryManager mgr =
                control.createMock(DestinationFactoryManager.class);
            DestinationFactory factory =
                control.createMock(DestinationFactory.class);
            Destination destination =
                control.createMock(Destination.class);

            bus.setExtension(mgr, DestinationFactoryManager.class);
            mgr.getDestinationFactoryForUri(decoupledURL.toString());
            EasyMock.expectLastCall().andReturn(factory);
            factory.getDestination(EasyMock.isA(EndpointInfo.class));
            EasyMock.expectLastCall().andReturn(destination);
            destination.setMessageObserver(EasyMock.isA(HTTPConduit.InterposedMessageObserver.class));
        }
        
        control.replay();
        
        HTTPConduit conduit = new HTTPTestConduit(bus, 
                                              endpointInfo,
                                              null,
                                              connectionFactory);
        conduit.finalizeConfig();

        if (send) {
            conduit.getClient().setConnectionTimeout(303030);
            conduit.getClient().setReceiveTimeout(404040);
            conduit.getClient().setAutoRedirect(autoRedirect);
            if (!autoRedirect) {
                conduit.getClient().setAllowChunking(true);
            }
        }

        if (decoupled) {
            conduit.getClient().setDecoupledEndpoint(decoupledURL.toString());
            assertNotNull("expected back channel", conduit.getBackChannel());
        } else {
            assertNull("unexpected back channel", conduit.getBackChannel());
        }

        observer = new MessageObserver() {
            public void onMessage(Message m) {
                inMessage = m;
            }
        };
        conduit.setMessageObserver(observer);
        return conduit;
    }
    

    private void verifySentMessage(HTTPConduit conduit, Message message)
        throws IOException {
        verifySentMessage(conduit, message, false);
    }

    private void verifySentMessage(HTTPConduit conduit,
                                   Message message,
                                   boolean expectHeaders)
        throws IOException {
        verifySentMessage(conduit, message, expectHeaders, false);
    }
    
    private void verifySentMessage(HTTPConduit conduit,
                                   Message message,
                                   boolean expectHeaders,
                                   boolean decoupled)
        throws IOException {
        control.verify();
        control.reset();

        OutputStream wrappedOS = verifyRequestHeaders(message, expectHeaders);

        os.write(PAYLOAD.getBytes(), 0, PAYLOAD.length());
        EasyMock.expectLastCall();
        
        os.flush();
        EasyMock.expectLastCall();
        os.flush();
        EasyMock.expectLastCall();
        os.close();
        EasyMock.expectLastCall();
        
        verifyHandleResponse(decoupled);

        control.replay();
        
        wrappedOS.flush();
        wrappedOS.flush();
        wrappedOS.close();
        
        assertNotNull("expected in message", inMessage);
        Map<?, ?> headerMap = (Map<?, ?>) inMessage.get(Message.PROTOCOL_HEADERS);
        assertEquals("unexpected response headers", headerMap.size(), 0);
        Integer expectedResponseCode = decoupled 
                                       ? HttpURLConnection.HTTP_ACCEPTED
                                       : HttpURLConnection.HTTP_OK;
        assertEquals("unexpected response code",
                     expectedResponseCode,
                     inMessage.get(Message.RESPONSE_CODE));
        assertTrue("unexpected content formats",
                   inMessage.getContentFormats().contains(InputStream.class));
        assertSame("unexpected content", is, inMessage.getContent(InputStream.class));
        
        if (decoupled) {
            verifyDecoupledResponse(conduit);
        }
        
        conduit.close();
        
        finalVerify();
    }

    private OutputStream verifyRequestHeaders(Message message, boolean expectHeaders)
        throws IOException {
        Map<String, List<String>> headers =
            CastUtils.cast((Map<?, ?>)message.get(Message.PROTOCOL_HEADERS));
        assertNotNull("expected request headers set", headers);
        assertTrue("expected output stream format",
                   message.getContentFormats().contains(OutputStream.class));
        
        connection.getRequestMethod();
        EasyMock.expectLastCall().andReturn("POST");

        os = EasyMock.createMock(ServletOutputStream.class);
        connection.getOutputStream();
        EasyMock.expectLastCall().andReturn(os);
        
        message.put(HTTPConduit.KEY_HTTP_CONNECTION, connection);
        if (expectHeaders) {
            connection.addRequestProperty(EasyMock.eq("Authorization"),
                                          EasyMock.eq("Basic Qko6dmFsdWU="));            
            EasyMock.expectLastCall();
            connection.addRequestProperty(EasyMock.eq("content-type"),
                                          EasyMock.eq("text/xml"));
            EasyMock.expectLastCall();
            connection.addRequestProperty(EasyMock.eq("content-type"),
                                          EasyMock.eq("charset=utf8"));
            EasyMock.expectLastCall();
        }
        
        control.replay();
        
        OutputStream wrappedOS = message.getContent(OutputStream.class);
        assertNotNull("expected output stream", wrappedOS);
        
        wrappedOS.write(PAYLOAD.getBytes());
        
        control.verify();
        control.reset();

        return wrappedOS;
    }
    
    private void verifyHandleResponse(boolean decoupled) throws IOException {
        connection.getHeaderFields();
        EasyMock.expectLastCall().andReturn(Collections.EMPTY_MAP);
        int responseCode = decoupled 
                           ? HttpURLConnection.HTTP_ACCEPTED
                           : HttpURLConnection.HTTP_OK;
        connection.getResponseCode();
        EasyMock.expectLastCall().andReturn(responseCode).anyTimes();
        connection.getErrorStream();
        EasyMock.expectLastCall().andReturn(null);
        is = EasyMock.createMock(ServletInputStream.class);
        connection.getInputStream();
        EasyMock.expectLastCall().andReturn(is);
    }
    
    private void verifyDecoupledResponse(HTTPConduit conduit)
        throws IOException {
        Message incoming = new MessageImpl();
        conduit.getDecoupledObserver().onMessage(incoming);
        assertSame("expected pass thru onMessage() notification",
                   inMessage,
                   incoming);
        assertEquals("unexpected response code",
                     HttpURLConnection.HTTP_OK,
                     inMessage.get(Message.RESPONSE_CODE));
        assertEquals("expected DECOUPLED_CHANNEL_MESSAGE flag set",
                     Boolean.TRUE,
                     inMessage.get(DECOUPLED_CHANNEL_MESSAGE));
        assertEquals("unexpected HTTP_REQUEST set",
                     false,
                     inMessage.containsKey(AbstractHTTPDestination.HTTP_REQUEST));
        assertEquals("unexpected HTTP_RESPONSE set",
                     false,
                     inMessage.containsKey(AbstractHTTPDestination.HTTP_RESPONSE));
        assertEquals("unexpected Message.ASYNC_POST_RESPONSE_DISPATCH set",
                     false,
                     inMessage.containsKey(Message.ASYNC_POST_RESPONSE_DISPATCH));
    }

    private void finalVerify() {
        if (control != null) {
            control.verify();
            control = null;
        }
    }
    
}
