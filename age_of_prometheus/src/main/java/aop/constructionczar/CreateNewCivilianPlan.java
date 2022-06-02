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
public class CreateNewCivilianPlan extends Plan
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
        System.out.println("Create New Civilian plan running...");

        // Check if we have enough resources - make a house cost 2 food
        if( ResourceManager.getInstance().requestResource(1, ResourceManager.FOOD, 2) )
		{
			// If we're here, we can build a house
			IVector2 createloc = getCreateCivilianLoc(env);

			Map props = new HashMap();
			props.put(Space2D.PROPERTY_POSITION, createloc);
			env.createSpaceObject("civilian", props, null);
			ResourceManager.getInstance().addResource(1,ResourceManager.POPULATION, 1);
		}
		else
		{
			// If we're here, not enough food. Signal that we need more.
			System.out.println("Need food");
			ConstructionPolicy.getInstance().setResourceNeed(1, ResourceManager.FOOD);
		}
    }
	private IVector2 getCreateCivilianLoc(Grid2D env)
	{
		// Create a civilian in first empty square in 10x10 grid in top left corner
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
