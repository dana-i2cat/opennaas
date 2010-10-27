package cat.i2cat.manticore.test;

import org.apache.log4j.BasicConfigurator;
import org.junit.runner.JUnitCore;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// Turn on log4j, this will read log4j.properties
		BasicConfigurator.configure();
		
		// Run tests
		JUnitCore.runClasses( BGP.class );
	}
}
