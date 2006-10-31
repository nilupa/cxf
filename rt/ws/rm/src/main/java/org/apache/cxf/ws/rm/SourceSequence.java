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

package org.apache.cxf.ws.rm;

import java.math.BigInteger;
import java.util.Date;

public interface SourceSequence {
    
    /**
     * @return the sequence identifier
     */
    Identifier getIdentifier();
    
    /**
     * @return the message number assigned to the most recent outgoing application message.
     */
    BigInteger getCurrentMessageNr();
    
    /**
     * @return true if the last message had been sent for this sequence. 
     */
    boolean isLastMessage();
    
    /**
     * @return the identifier of the sequence that was created on behalf of the CreateSequence request 
     * that included this sequence as an offer
     */
    Identifier getOfferingSequenceIdentifier();
    
    /**
     * @return the identifier of the rm source
     */
    String getEndpointIdentifier(); 
    
    /**
     * @return the expiry data of this sequence
     */
    Date getExpires();
    
    /**
     * Returns true if this sequence was constructed from an offer for an inbound sequence
     * includes in the CreateSequenceRequest in response to which the sequence with
     * the specified identifier was created.
     * 
     * @param id the sequence identifier
     * @return true if the sequence was constructed from an offer.
     */
    boolean offeredBy(Identifier sid);
}
