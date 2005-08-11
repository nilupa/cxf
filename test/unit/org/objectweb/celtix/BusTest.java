package org.objectweb.celtix;

import java.util.HashMap;
import junit.framework.*;

import org.objectweb.celtix.bus.CeltixBus;


public class BusTest extends TestCase {


    public void testBusInit() throws Exception {
        Bus bus = Bus.init(null, new HashMap<String, Object>());
        assertNotNull(bus);
        assertTrue("Bus not a Celtix bus", bus instanceof CeltixBus);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(BusFactory.BUS_CLASS_PROPERTY, "com.foo.bar.Bus");
        try {
            bus = Bus.init(null, map);            
            fail("Should have thrown an exception");
        } catch (BusException ex) {
            //ignore -expected
        }
    }
}

