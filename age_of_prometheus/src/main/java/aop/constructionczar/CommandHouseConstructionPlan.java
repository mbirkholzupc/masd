package aop.constructionczar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import aop.ConstructionPolicy;
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
import jadex.extension.envsupport.math.Vector2Int;

/**
 *  The plan to start construction of a house
 */
public class CommandHouseConstructionPlan extends Plan
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
        System.out.println("Command House Construction plan running...");

        // Check if we have enough resources - make a house cost 10 wood
        if( ResourceManager.getInstance().requestResource(1, ResourceManager.WOOD, 10) )
		{
			// If we're here, we can build a house
			System.out.println("Can build house");
			IVector2 buildloc = getHouseBuildLoc(env);
			ConstructionPolicy.getInstance().submitRequest(1,ConstructionPolicy.HOUSE,buildloc.getXAsInteger(), buildloc.getYAsInteger());
		}
		else
		{
			// If we're here, not enough wood. Signal that we need more.
			System.out.println("Need wood");
			ConstructionPolicy.getInstance().setResourceNeed(1, ResourceManager.WOOD);
		}
    }
	private IVector2 getHouseBuildLoc(Grid2D env)
	{
		// Create a house in first empty square in 10x10 grid in top left corner
		int x=0;
		int y=0;
		boolean found_empty = false;
		for(x=0; x<10; x++) {
			for(y=0; y<10; y++) {
				Set<ISpaceObject> thingsinsquare = env.getNearGridObjects(new Vector2Int(x, y),0,null);
				if(thingsinsquare.isEmpty())
				{
					found_empty = true;
				}
				if(found_empty) break;
			}
			if(found_empty) break;
		}

		return new Vector2Int(x,y);
	}

}
