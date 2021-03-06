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

package org.apache.cxf.service.factory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;

/**
 * 
 */
@NoJSR250Annotations(unlessNull = "bus")
public class FactoryBeanListenerManager {
    Bus bus;
    
    List<FactoryBeanListener> listeners
        = new CopyOnWriteArrayList<FactoryBeanListener>();
    
    public FactoryBeanListenerManager() {
        listeners.add(new AnnotationsFactoryBeanListener());
    }
    public FactoryBeanListenerManager(Bus b) {
        this();
        setBus(b);
    }
    
    @Resource
    public final void setBus(Bus bus) {
        this.bus = bus;
        this.bus.setExtension(this, FactoryBeanListenerManager.class);
    }
    
    public List<FactoryBeanListener> getListeners() {
        return listeners;
    }
    
    public void addListener(FactoryBeanListener l) {
        listeners.add(l);
    }
    
    public void removeListener(FactoryBeanListener l) {
        listeners.remove(l);
    }
    
}
