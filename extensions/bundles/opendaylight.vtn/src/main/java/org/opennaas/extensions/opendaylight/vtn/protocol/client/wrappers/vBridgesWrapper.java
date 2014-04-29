package org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers;

import java.util.ArrayList;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;

/**
 * Object wrapper of ArrayList {@link vBridges} to allow custom JSON
 * deserialization
 *
 * @author Jospe Batallé
 */
public class vBridgesWrapper extends ArrayList<OpenDaylightvBridge> {

    private static final long serialVersionUID = -3635412232071232706L;

}
