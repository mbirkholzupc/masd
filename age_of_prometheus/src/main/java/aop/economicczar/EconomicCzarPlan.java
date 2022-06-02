package aop.economicczar;

import java.util.HashMap;
import java.util.Map;

import aop.ConstructionPolicy;
import aop.EconomicPolicy;
import aop.MoveAction;
import aop.ResourceManager;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  The behavior of the civilian. 
 */
public class EconomicCzarPlan extends Plan
{
	int cur_resource = 0;

	public int getManhattanDistance(IVector2 pos1, IVector2 pos2){
		int xDiff = Math.abs(pos1.getX().getAsInteger() - pos2.getX().getAsInteger());
		int yDiff = Math.abs(pos1.getY().getAsInteger() - pos2.getY().getAsInteger());
		return xDiff + yDiff;
	}
	/**
	 *  Plan body.
	 */
	public void body()
	{
        Grid2D	env	= (Grid2D)getBeliefbase().getBelief("env").getFact();
        ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();

		while(true) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}

			// Check if other czars have resource requests
			int construction_czar_request = ConstructionPolicy.getInstance().getResourceNeed(1);

			EconomicPolicy ep = EconomicPolicy.getInstance();
			ResourceManager rm = ResourceManager.getInstance();
			if(construction_czar_request!=ResourceManager.NONE) {
				ep.setNeededResource(0,construction_czar_request);
			}
			else {
				// If nothing special requested, set to resource we have the least of
				int food_level = rm.readResource(1,ResourceManager.FOOD);
				int wood_level = rm.readResource(1,ResourceManager.WOOD);

				if( food_level < wood_level ) ep.setNeededResource(1, ResourceManager.FOOD);
				if( wood_level < food_level ) ep.setNeededResource(1, ResourceManager.WOOD);
			}
		}
    }
}
