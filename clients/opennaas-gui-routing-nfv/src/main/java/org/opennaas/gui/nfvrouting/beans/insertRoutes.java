/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.gui.nfvrouting.beans;

import org.opennaas.gui.nfvrouting.entities.Route;
import org.springframework.util.AutoPopulatingList;

/**
 *
 * @author josep
 */
public class insertRoutes {
    
    private AutoPopulatingList<Route> listRoutes = new AutoPopulatingList<Route>(Route.class);
    
    public insertRoutes(){
        listRoutes = new AutoPopulatingList<Route>(Route.class);
    }

    public AutoPopulatingList<Route> getListRoutes() {
        return listRoutes;
    }

    public void setListRoutes(AutoPopulatingList<Route> listRoutes) {
        this.listRoutes = listRoutes;
    }
    
    public void addListRoutes(Route route){
        listRoutes.add(route);
    }
}
