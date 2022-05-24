package aop;

import jadex.application.EnvironmentService;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.extension.envsupport.environment.space2d.Grid2D;

/**
 *  Main for starting the example programmatically.
 *  
 *  To start the example via this Main.java Jadex platform 
 *  as well as examples must be in classpath.
 */
public class LaunchAOP
{
	/**
	 *  Start a platform and the example.
	 */
	public static void main(String[] args) 
	{
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefaultNoGui()).get();
		CreationInfo ci = new CreationInfo().setFilename("AgeOfPrometheus.application.xml");
		platform.createComponent(ci).get();

		// The InitMapProcess:start method is called at startup and initializes things.

		// EXAMPLE CODE TO SHOW RESOURCE MANAGER USAGE (TODO: Delete after it is actually used in code)
		ResourceManager rm = ResourceManager.getInstance();
		rm.addResource(0, ResourceManager.WOOD, 50);
		rm.addResource(0, ResourceManager.WOOD, 50);
		rm.addResource(0, ResourceManager.FOOD, 25);
		System.out.println("Team 0 resources: " + rm.readResource(0,ResourceManager.FOOD) + " " +
		                   rm.readResource(0,ResourceManager.WOOD) + " " +
						   rm.readResource(0,ResourceManager.ORE) );
		System.out.println("Team 1 resources: " + rm.readResource(1,ResourceManager.FOOD) + " " +
				rm.readResource(1,ResourceManager.WOOD) + " " +
				rm.readResource(1,ResourceManager.ORE) );
		// END EXAMPLE CODE TO SHOW RESOURCE MANAGER USAGE
	}
}
