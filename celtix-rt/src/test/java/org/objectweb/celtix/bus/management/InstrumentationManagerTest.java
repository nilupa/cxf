package org.objectweb.celtix.bus.management;

import java.util.List;
import junit.framework.TestCase;

import org.objectweb.celtix.Bus;
import org.objectweb.celtix.BusException;
import org.objectweb.celtix.bus.busimpl.ComponentCreatedEvent;
import org.objectweb.celtix.bus.busimpl.ComponentRemovedEvent;

import org.objectweb.celtix.bus.workqueue.WorkQueueInstrumentation;
import org.objectweb.celtix.bus.workqueue.WorkQueueManagerImpl;
import org.objectweb.celtix.management.Instrumentation;
import org.objectweb.celtix.management.InstrumentationManager;


public class InstrumentationManagerTest extends TestCase {
    Bus bus;
    InstrumentationManager im;    
    
    public void setUp() throws Exception {      
        WorkQueueInstrumentation.resetInstanceNumber();
        bus = Bus.init();       
        im = bus.getInstrumentationManager();
    }
    
    public void tearDown() throws Exception {
        bus.shutdown(true);        
    }
    
    // try to get WorkQueue information
    public void testWorkQueueInstrumentation() throws BusException {
        //im.getAllInstrumentation();
        WorkQueueManagerImpl wqm = new WorkQueueManagerImpl(bus);
        bus.sendEvent(new ComponentCreatedEvent(wqm));        
        bus.sendEvent(new ComponentCreatedEvent(wqm));
        // TODO should test for the im getInstrumentation 
        List<Instrumentation> list = im.getAllInstrumentation();        
        assertEquals("Too many instrumented items", 2, list.size());
        Instrumentation it1 = list.get(0);
        Instrumentation it2 = list.get(1);
        assertTrue("Item 1 not a WorkQueueInstrumentation",
                   WorkQueueInstrumentation.class.isAssignableFrom(it1.getClass()));
        assertTrue("Item 2 not a WorkQueueInstrumentation",
                   WorkQueueInstrumentation.class.isAssignableFrom(it2.getClass()));
        
        assertEquals("Item 1's name is not correct",
                     it1.getInstrumentationName() + "0",
                     it1.getUniqueInstrumentationName());
        assertEquals("Item 2's name is not correct",
                     it2.getInstrumentationName() + "1",
                     it2.getUniqueInstrumentationName());
        
        bus.sendEvent(new ComponentRemovedEvent(wqm));
        assertEquals("Instrumented stuff not removed from list", 0, list.size());
    }
      

}
