package org.objectweb.celtix.bus;

import java.util.Map;

import org.objectweb.celtix.Bus;
import org.objectweb.celtix.BusException;
import org.objectweb.celtix.bindings.BindingManager;
import org.objectweb.celtix.bus.workqueue.WorkQueueManagerImpl;
import org.objectweb.celtix.buslifecycle.BusLifeCycleManager;
import org.objectweb.celtix.configuration.Configuration;
import org.objectweb.celtix.handlers.HandlerFactoryManager;
import org.objectweb.celtix.plugins.PluginManager;
import org.objectweb.celtix.transports.TransportFactoryManager;
import org.objectweb.celtix.workqueue.WorkQueueManager;
import org.objectweb.celtix.wsdl.WSDLManager;

public class CeltixBus extends Bus {
    
    private Configuration configuration;
    private BindingManager bindingManager;
    private Object clientRegistry;
    private HandlerFactoryManager handlerFactoryManager;
    private EndpointRegistry endpointRegistry;
    private TransportFactoryManager transportFactoryManager;
    private WSDLManager wsdlManager;
    private PluginManager pluginManager;
    private CeltixBusLifeCycleManager lifeCycleManager;
    private WorkQueueManager workQueueManager;
    
    /**
     * Protected constructor used by the <code>BusManager</code> to create a new bus.
     * 
     * @param busArgs the command line configuration of this <code>Bus</code>.
     */
    protected void initialize(String[] args, Map<String, Object> properties)
        throws BusException {

        lifeCycleManager = new CeltixBusLifeCycleManager();
        
        configuration = new BusConfiguration(args, properties);        
        wsdlManager = new WSDLManagerImpl(this);
        handlerFactoryManager = new HandlerFactoryManagerImpl(this);
        transportFactoryManager = new TransportFactoryManagerImpl(this);
        bindingManager = new BindingManagerImpl(this);
        workQueueManager = new WorkQueueManagerImpl(this);
        
        // create and initialise the remaining objects:
        // clientRegistry = new ClientRegistry(this);
        
        endpointRegistry = new EndpointRegistry(this);
        
        Bus.setCurrent(this); 
        
        lifeCycleManager.initComplete();
    }
    
    /**
     * Create and initialize a <code>Bus</code> object.
     * 
     * @param args Any args, such as domain name, configuration scope,
     * that may be needed to identify and initialize this <code>Bus</code>.
     * @return Bus If a <code>Bus</code> has already been created using the same args,
     * it will return the existing <code>Bus</code> object.  Otherwise,
     * it creates a new <code>Bus</code>.
     * @throws BusException If there is an error initializing <code>Bus</code>.
     */
    public static synchronized Bus init(String[] args) 
        throws BusException {
        return null;
    }
    
    /**
     * Shuts down the <code>Bus</code>.
     * 
     * @param wait If <code>true</code>, waits for the <code>Bus</code>
     * to shutdown before returning, otherwise returns immediately.
     * @throws BusException
     */
    public void shutdown(boolean wait) throws BusException {
        
        lifeCycleManager.preShutdown();
        
        // shutdown in inverse order of construction
        
        endpointRegistry.shutdown();
        
        // transportRegistry.shutdown(wait);
        // 
        // handlerRegistry.shutdown(wait);
        // clientRegistry.shutdown(wait);
        // bindingManager.shutdown(wait);        
        // configuration.shutdown();
        
        workQueueManager.shutdown(wait);

        lifeCycleManager.postShutdown();
    }   
    
    /* (non-Javadoc)
     * @see org.objectweb.celtix.Bus#run()
     */
    @Override
    public void run() {
        workQueueManager.run();
    }

    /** 
     * Returns the <code>Configuration</code> of this <code>Bus</code>.
     * 
     * @return Configuration the configuration of this <code>bus</code>.
     */
    public Configuration getConfiguration() {
        return configuration;
    }
    
    /** 
     * Returns the <code>HandlerRegistry</code> of this <code>Bus</code>.
     * 
     * @return HandlerRegistry the servant registry of this <code>Bus</code>.
     */
    public HandlerFactoryManager getHandlerFactoryManager() {
        return handlerFactoryManager;
    }

    /** 
     * Returns the <code>BindingManager</code> of this <code>Bus</code>.
     * 
     * @return BindingManager of this <code>Bus</code>.
     */
    public BindingManager getBindingManager() {
        return bindingManager;
    }
    
    /** 
     * Returns the <code>TransportRegistry</code> of this <code>Bus</code>.
     * 
     * @return TransportRegistry the servant registry of this <code>Bus</code>.
     */
    public TransportFactoryManager getTransportFactoryManager() {
        return transportFactoryManager;
    }
    
    /** 
     * Returns the <code>ClientRegistry</code> of this <code>Bus</code>.
     * 
     * @return ClientRegistry the client registry of this <code>Bus</code>.
     */
    public Object getClientRegistry() {
        return clientRegistry;
    }
    
    
    public WSDLManager getWSDLManager() {
        return wsdlManager;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.celtix.Bus#getPluginManager()
     */
    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }
    
    /** 
     * Returns the <code>EndpointRegistry</code> of this <code>Bus</code>.
     * 
     * @return EndpointRegistry the endpoint registry of this <code>Bus</code>.
     */
    Object getEndpointRegistry() {
        return endpointRegistry;
    }

    /* (non-Javadoc)
     * @see org.objectweb.celtix.Bus#getLifeCycleManager()
     */
    @Override
    public BusLifeCycleManager getLifeCycleManager() {
        return lifeCycleManager;
    }

    /* (non-Javadoc)
     * @see org.objectweb.celtix.Bus#getWorkQueueManager()
     */
    @Override
    public WorkQueueManager getWorkQueueManager() {
        return workQueueManager;
    }

    
    
    
}
