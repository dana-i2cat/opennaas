package org.opennaas.extensions.network.model.topology;

/*
 * #%L
 * OpenNaaS :: Network :: Model
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * A Broadcast Segment is a direct (not concatenated) connection between multiple Interfaces. A Link can only connect two Interface, while a Broadcast
 * Segment connects multiple interfaces.
 * 
 * A Broadcast Segment can be unidirectional, bidirectional, point-to-point, point-to-multipoint, or multipoint-to-multipoint. Data coming from a
 * connected interface (Interface linkTo Broadcast Segment) will be forwarded to all other Interfaces (Broadcast Segment linkTo Interfaces). Thus the
 * received data will be forwarded to all Interfaces, with the exception of the Interface were the data originated.
 * 
 * @author isart
 * 
 */
public class BroadcastSegment extends NetworkConnection {

}
