package net.i2cat.mantychore.commons;

import java.util.List;

public interface ICapability {
		//FIXME IT IS CORRECT SPECIFY IN THIS PLACE AN ERROR
	public final static  String ERROR_CAPABILITY = "id operation does not exist for this capability";
	public void setResource (Object model);
	public Object getResource ();
	public Response sendMessage (String idOperation, Object paramsModel);
	public List<String> getIdMessages ();
	public void initialize ();



}
