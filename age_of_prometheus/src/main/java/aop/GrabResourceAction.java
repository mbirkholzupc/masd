package aop;

import java.util.Map;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;


/**
 * Action for eating food or another creature.
 */
public class GrabResourceAction extends SimplePropertyObject implements ISpaceAction
{
    // -------- constants --------

    /** The property for the points of a creature. */
    public static final String	PROPERTY_WOOD	= "wood";

    // -------- IAgentAction interface --------

    public int getManhattanDistance(IVector2 pos1, IVector2 pos2){
        int xDiff = Math.abs(pos1.getX().getAsInteger() - pos2.getX().getAsInteger());
        int yDiff = Math.abs(pos1.getY().getAsInteger() - pos2.getY().getAsInteger());
        return xDiff + yDiff;
    }

    /**
     * Performs the action.
     *
     * @param parameters parameters for the action
     * @param space the environment space
     * @return action return value
     */
    public Object perform(Map parameters, IEnvironmentSpace space)
    {
        // System.out.println("eat action: "+parameters);
        System.out.println("GRABBING...");
        Grid2D grid = (Grid2D)space;
        IComponentDescription owner = (IComponentDescription)parameters.get(ISpaceAction.ACTOR_ID);
        ISpaceObject avatar = grid.getAvatar(owner);
        final ISpaceObject target = (ISpaceObject)parameters.get(ISpaceAction.OBJECT_ID);

        if(null == space.getSpaceObject(target.getId()))
        {
            throw new RuntimeException("No such object in space: " + target);
        }

        // prohibit agents to grab resources at
        if(getManhattanDistance((IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION), (IVector2)target.getProperty(Space2D.PROPERTY_POSITION)) > 1){
            throw new RuntimeException("Can only grab objects at distance <= 1.");
        }

        Integer wood = (Integer)avatar.getProperty(PROPERTY_WOOD);
        if(avatar.getType().equals("prey") && target.getType().equals("tree"))
        {
            wood = wood != null ? Integer.valueOf(wood.intValue() + 1) : Integer.valueOf(1);
        }
        else
        {
            throw new RuntimeException("Objects of type '" + avatar.getType() + "' cannot eat objects of type '" + target.getType() + "'.");
        }

        space.destroySpaceObject(target.getId());

        // Todo: Use listener model for self destroying of agent!?
        if(target.getProperty(ISpaceObject.PROPERTY_OWNER) != null)
        {
            // System.err.println("Destroying: "+target.getProperty(ISpaceObject.PROPERTY_OWNER));
            space.getExternalAccess().getExternalAccess(((IComponentDescription)target.getProperty(ISpaceObject.PROPERTY_OWNER)).getName()).killComponent();
        }
        for (Object spaceObject:
             grid.getSpaceObjectsCollection()) {
            SpaceObject spaceObjectCasted = (SpaceObject)spaceObject;

            if(spaceObjectCasted.getType() == "prey"){
                spaceObjectCasted.setProperty(PROPERTY_WOOD, wood);
            }
        }
        for (Object spaceObject:
                grid.getSpaceObjectsCollection()) {
            SpaceObject spaceObjectCasted = (SpaceObject)spaceObject;

            if(spaceObjectCasted.getType() == "prey"){
                System.out.println(spaceObjectCasted.getProperty(PROPERTY_WOOD));
            }
        }
        // System.out.println("Object eaten: "+target);

        return null;
    }
}
