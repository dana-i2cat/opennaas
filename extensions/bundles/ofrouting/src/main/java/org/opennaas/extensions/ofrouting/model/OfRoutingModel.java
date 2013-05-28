package org.opennaas.extensions.ofrouting.model;

import java.util.ArrayList;
import java.util.List;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

/**
 *
 * @author josep
 */
public class OfRoutingModel implements IModel{
    
    private Table table;

    @Override
    public List<String> getChildren() {
        return new ArrayList<String>();
    }

    @Override
    public String toXml() throws SerializationException {
        return ObjectSerializer.toXml(this);
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
    
}
