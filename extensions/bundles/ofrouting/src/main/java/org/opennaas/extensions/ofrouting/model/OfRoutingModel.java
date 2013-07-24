package org.opennaas.extensions.ofrouting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

/**
 *
 * @author josep
 */
public class OfRoutingModel implements IModel{
    
    private Table table;
    private Map<String, String> switchController = new HashMap<String, String>();

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

   public Map<String, String> getSwitchController() {
        return switchController;
    }

    public void setSwitchController(Map<String, String> switchController) {
        this.switchController = switchController;
    }       
}