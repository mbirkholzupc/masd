package aop.economicczar;

import java.util.HashMap;
import java.util.Map;

import aop.EconomicPolicy;
import aop.MoveAction;
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

        System.out.println("Economic Czar plan running...");
		while(true) {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}

			cur_resource=(cur_resource==0)?1:0;
			EconomicPolicy.getInstance().setNeededResource(0,cur_resource);
			System.out.println("Changed needed resource to: " + EconomicPolicy.getInstance().readNeededResource(0));

		}
    }
}
