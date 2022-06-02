package aop.civilian;

import java.util.HashMap;
import java.util.Map;

import aop.EconomicPolicy;
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
 *  The behavior of the dumb prey.
 */
public class CollectResourcesPlan extends Plan
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
		EconomicPolicy ep = EconomicPolicy.getInstance();
		while(true)
		{
			int needed_resourse = ep.readNeededResource(0);
			String beliefe_to_check = needed_resourse == ResourceManager.FOOD ? "nearest_wild_food" : "nearest_tree";
			if(false/*build house message received*/){
				IGoal build_house_goal = createGoal("build_house_goal");
				dispatchSubgoalAndWait(build_house_goal);
			}
//			System.out.println("nearest food for: "+getAgentName()+", "+getBeliefbase().getBelief("nearest_food").getFact());

			// Get current position.
			IVector2	pos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);

			ISpaceObject	tree	= (ISpaceObject)getBeliefbase().getBelief(beliefe_to_check).getFact();
			boolean isOneStepAway = false;
			if(tree!=null){
				isOneStepAway = getManhattanDistance(pos, (IVector2)tree.getProperty(Space2D.PROPERTY_POSITION)) == 1;
			}
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
					// getBeliefbase().getBelief("belife_to_check").setFact(null);
				}
				catch(RuntimeException e)
				{
				}
			}

			else
			{
				// Move towards the tree, if any
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
						getBeliefbase().getBelief(beliefe_to_check).setFact(null);
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
					getBeliefbase().getBelief(beliefe_to_check).setFact(null);

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
