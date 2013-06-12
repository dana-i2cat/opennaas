package org.opennaas.gui.nfvrouting.beans;

import org.opennaas.gui.nfvrouting.entities.ControllerInfo;
import org.springframework.util.AutoPopulatingList;

/**
 *
 * @author josep
 */
public class insertCtrlInfo {
    
    private AutoPopulatingList<ControllerInfo> listCtrl = new AutoPopulatingList<ControllerInfo>(ControllerInfo.class);
    
    public insertCtrlInfo(){
        listCtrl = new AutoPopulatingList<ControllerInfo>(ControllerInfo.class);
    }

    public void addListCtrl(ControllerInfo route){
        listCtrl.add(route);
    }

    public AutoPopulatingList<ControllerInfo> getListCtrl() {
        return listCtrl;
    }

    public void setListCtrl(AutoPopulatingList<ControllerInfo> listCtrl) {
        this.listCtrl = listCtrl;
    }

}
