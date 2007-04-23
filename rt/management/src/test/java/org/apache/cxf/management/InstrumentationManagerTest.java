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
package org.apache.cxf.management;

import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import junit.framework.TestCase;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.workqueue.WorkQueueManagerImpl;

public class InstrumentationManagerTest extends TestCase {
    InstrumentationManager im;
    Bus bus;
    
    public void setUp() throws Exception {

    }
    
    public void tearDown() throws Exception {
        //test case had done the bus.shutdown
        bus.shutdown(true);
    }
    
    public void testInstrumentationNotEnabled() {
        SpringBusFactory factory = new SpringBusFactory();
        bus =  factory.createBus();
        im = bus.getExtension(InstrumentationManager.class);
        assertTrue("Instrumentation Manager should not be null", im != null);
        MBeanServer mbs = im.getMBeanServer();
        assertNull("MBeanServer should not be available.", mbs);
    }
    
    // try to get WorkQueue information
    public void testWorkQueueInstrumentation() throws Exception {
        SpringBusFactory factory = new SpringBusFactory();
        bus =  factory.createBus("managed-spring.xml", true);
        im = bus.getExtension(InstrumentationManager.class);
        assertTrue("Instrumentation Manager should not be null", im != null);
        WorkQueueManagerImpl wqm = new WorkQueueManagerImpl();
        wqm.setBus(bus);
        wqm.getAutomaticWorkQueue();
        
        MBeanServer mbs = im.getMBeanServer();
        assertNotNull("MBeanServer should be available.", mbs);
        ObjectName name = new ObjectName(ManagementConstants.DEFAULT_DOMAIN_NAME 
                                         + ":type=WorkQueueMBean,*");
        Set s = mbs.queryNames(name, null);
        assertTrue(s.size() == 1);
        Iterator it = s.iterator();
        while (it.hasNext()) {
            ObjectName n = (ObjectName)it.next();            
            Long result = 
                (Long)mbs.invoke(n, "getWorkQueueMaxSize", new Object[0], new String[0]);            
            assertEquals(result, Long.valueOf(250));
        }

        bus.shutdown(true);
    }

}
