package aop.testman;

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
import jadex.extension.envsupport.math.Vector2Int;

//import aop.InitMapProcess.grid;


import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;

import static jadex.micro.KernelMultiAgent.props;

/**
 *  The behavior of the Testman.
 */
public class TestmanPlan extends Plan
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

        // Boolean to help if agent should go one way or the other
        boolean flag = true;

        while(true)
        {
            // Get current position.
            IVector2	pos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
            System.out.println("current pos:     " + pos);



            // excuse my lack of java skills
            int y = Integer.parseInt(pos.getY().toString());
            int x = Integer.parseInt(pos.getX().toString());

            if (flag == true) {
                // Go to top right corner
                if (y > (15) && Math.random() > 0.5) {
                    lastdir = MoveAction.DIRECTION_UP;
                } else if (x < 35) {
                    lastdir = MoveAction.DIRECTION_RIGHT;
                } else {
                    flag = false;
                    // this creates a new agent, in this case a civilian
                    env.createSpaceObject("civilian", props, null);
                }
            } else{
                // Go to top right corner
                if (y < (35) && Math.random() > 0.5) {
                    lastdir = MoveAction.DIRECTION_DOWN;
                } else if (x > 15) {
                    lastdir = MoveAction.DIRECTION_LEFT;
                } else {
                    flag = true;

                    //create a house
                    env.createSpaceObject("house_1", props, null);
                    //env.createSpaceObject("tree", props, null);

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
                // Move failed, turn 90 degrees. 10 times
                for (int i = 0; i < 10; i++){
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
