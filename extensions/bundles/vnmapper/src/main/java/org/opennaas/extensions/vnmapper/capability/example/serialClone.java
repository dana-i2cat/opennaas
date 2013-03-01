/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.vnmapper.capability.example;

import java.io.*;

    
 class ObjectCopier {
  public static Object deepCopy(Object original) throws IOException 
       {
      ObjectInputStream ois;
      ObjectOutputStream oos;
      ByteArrayInputStream bais;
      ByteArrayOutputStream baos;
      byte [] data;
      Object copy=null;

      // write object to bytes
      baos = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(baos);
      oos.writeObject(original);
      oos.close();

      // get the bytes
      data = baos.toByteArray();

      // construct an object from the bytes
      
      bais = new ByteArrayInputStream(data);
      ois = new ObjectInputStream(bais);
      try{
      copy = ois.readObject();
      }catch (Exception e){}
      ois.close();
      
      return copy;
   }
}

