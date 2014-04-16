package org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers;

import java.util.ArrayList;
import org.opennaas.extensions.opendaylight.vtn.model.Switch;

/**
 * Object wrapper of ArrayList {@link LogicPort} to allow custom JSON
 * deserialization
 *
 * @author Jospe Batallé
 */
public class SwitchesWrapper extends ArrayList<Switch> {

    private static final long serialVersionUID = -3635412232071232706L;

}
