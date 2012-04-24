package webservice;

import javax.jws.WebService;

/**
 * @author Jordi Puig
 */
@WebService
public interface ITestService {

	/**
	 * @return
	 */
	public String test();

}
