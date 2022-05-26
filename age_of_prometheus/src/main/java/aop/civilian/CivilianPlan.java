package aop.civilian;

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
public class CivilianPlan extends Plan
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

        while(true)
        {
            // Get current position.
            IVector2	pos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);

            // Turn 90 degrees with probability 0.25, otherwise continue moving in same direction.
            if(lastdir==null || Math.random()>0.75)
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
                // Move failed, turn 90 degrees.

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
