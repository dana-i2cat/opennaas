package bundle.internal;

import bundle.MyService;

public class MyServiceImpl implements MyService {

	@Override
	public String sayHello(String name) {
		return "Hello " + name;
	}
}
