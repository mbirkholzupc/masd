package aop.constructionczar;

import java.util.HashMap;
import java.util.Map;

import aop.ConstructionPolicy;
import aop.MoveAction;
import aop.ResourceManager;
import jadex.bdiv3.runtime.IGoal;
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
public class ConstructionCzarPlan extends Plan
{
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
        System.out.println("Construction Czar plan running...");

		while(true) {
			try {
				Thread.sleep(1000);  // Keep 1 second between updates
			} catch(Exception e) {
			}

			getBeliefbase().getBelief("need_more_houses").setFact(ResourceManager.getInstance().housingFull(1));
			boolean need_more_houses = (boolean)getBeliefbase().getBelief("need_more_houses").getFact();
			System.out.println("Belief need_more_houses: " + need_more_houses);

			if(need_more_houses)
			{
				IGoal house_goal = createGoal("command_house_construction_goal");
				dispatchSubgoalAndWait(house_goal);
				System.out.println("Done waiting.");
			}
			else
			{
				// Maybe we can make a new person. It's ok if it fails.
				IGoal house_goal = createGoal("create_new_civilian_goal");
				dispatchSubgoalAndWait(house_goal);
			}

		}
    }
}
