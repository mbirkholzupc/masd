package aop.constructionczar;

import java.util.HashMap;
import java.util.Map;

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
    }
}
