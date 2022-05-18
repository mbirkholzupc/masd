package aop.dumbprey;

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
 *  The behavior of the dumb prey.
 */
public class DumbPreyPlan extends Plan
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
		String	lastdir	= null;
		System.out.println("test1");
		while(true)
		{
//			System.out.println("nearest food for: "+getAgentName()+", "+getBeliefbase().getBelief("nearest_food").getFact());
			
			// Get current position.
			IVector2	pos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			
			ISpaceObject	tree	= (ISpaceObject)getBeliefbase().getBelief("nearest_tree").getFact();
			System.out.println("test2");
			boolean isOneStepAway = getManhattanDistance(pos, (IVector2)tree.getProperty(Space2D.PROPERTY_POSITION)) == 1;
			if(tree!=null && isOneStepAway)
			{
				// Perform eat action.
				try
				{
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
					params.put(ISpaceAction.OBJECT_ID, tree);
					Future<Void> fut = new Future<Void>();
					env.performSpaceAction("grab", params, new DelegationResultListener<Void>(fut));
					fut.get();
				}
				catch(RuntimeException e)
				{
//					System.out.println("Eat failed: "+e);
				}
			}

			else
			{
				// Move towards the food, if any
				if(tree!=null)
				{
					String	newdir	= MoveAction.getDirection(env, pos, (IVector2)tree.getProperty(Space2D.PROPERTY_POSITION));
					if(isOneStepAway){
						newdir = MoveAction.DIRECTION_NONE;
					}

					if(!MoveAction.DIRECTION_NONE.equals(newdir))
					{
						lastdir	= newdir;
					}
					else
					{
						// Tree unreachable.
						getBeliefbase().getBelief("nearest_tree").setFact(null);
					}
				}
				
				// When no food, turn 90 degrees with probability 0.25, otherwise continue moving in same direction.
				else if(lastdir==null || Math.random()>0.75)
				{
					if(MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir))
					{
						lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
					}
					else
					{
						lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
					}
				}
				
				// Perform move action.
				try
				{
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
					params.put(MoveAction.PARAMETER_DIRECTION, lastdir);
					Future<Void> fut = new Future<Void>();
					env.performSpaceAction("move", params, new DelegationResultListener<Void>(fut));
					fut.get();
				}
				catch(RuntimeException e)
				{
					// Move failed, forget about food and turn 90 degrees.
					getBeliefbase().getBelief("nearest_food").setFact(null);
					
//					System.out.println("Move failed: "+e);
					if(MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir))
					{
						lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
					}
					else
					{
						lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
					}
				}
			}
		}
	}
}
