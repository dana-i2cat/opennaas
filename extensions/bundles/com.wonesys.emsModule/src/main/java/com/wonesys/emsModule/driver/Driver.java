/*
 * Driver.java
 *
 * Created on 9 de abril de 2008, 15:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wonesys.emsModule.driver;

import java.util.List;

/**
 *
 * @author mbeltran
 */
public abstract class Driver {

	public abstract List getListTipos(String params);

	public abstract int getPuerto(String params);

}
