package webservice;

import javax.jws.WebService;

/**
 * @author Isart Canyameres
 * @author Jordi Puig
 */
@WebService
public class TestServiceImpl implements ITestService {

	/*
	 * (non-Javadoc)
	 */
	@Override
	public String test() {
		return "TEST!!!!!!";
	}

}
