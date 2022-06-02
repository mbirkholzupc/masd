package aop.dumbprey;

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
import jadex.extension.envsupport.math.Vector2Int;

import java.util.*;

public class BuildHousePlan extends Plan {
    @Override
    public void body() {
        Grid2D env	= (Grid2D)getBeliefbase().getBelief("env").getFact();
        ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
        // Get current position.
        IVector2 agentInitialPos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);

        // create a set containing all the coordinates of the grid that contains a tree
        IVector2 emptyGridPosition = env.getEmptyGridPosition();
        Set<String> treePositions = new HashSet<String>();
        Object[] iSpaceObjects = env.getSpaceObjects();
        for(int i=0; i<iSpaceObjects.length; i++){
            ISpaceObject iSpaceObject = (ISpaceObject)iSpaceObjects[i];
            if(Objects.equals(iSpaceObject.getType(), "tree")){
                IVector2 obstaclePosition = (IVector2)iSpaceObject.getProperty(Space2D.PROPERTY_POSITION);
                int xInt = obstaclePosition.getXAsInteger();
                int yInt = obstaclePosition.getYAsInteger();
                String stringifiedPosition = xInt + ";" + yInt;
                treePositions.add(stringifiedPosition);
            }
        }

        // Fill matrix of costs for A* algorithm
        int gridSize = env.getAreaSize().getLength().getAsInteger();
        int[][] onlineAStarGrid = new int[gridSize][gridSize];
        for (int i=0; i<gridSize; i++){
            for (int j=0; j<gridSize; j++){
                String stringifiedPosition = i+";"+j;
                boolean isEmpty = !treePositions.contains(stringifiedPosition);
                if(isEmpty){
                    IVector2 gridPos = new Vector2Int(i, j);
                    onlineAStarGrid[i][j] = MoveAction.getManhattanDistance(gridPos, emptyGridPosition);
                } else {
                    // huge number so it never wants to wolk through obstacles
                    onlineAStarGrid[i][j] = 9999999;
                }
            }
        }
        /* Print onlineAStarGrid
        System.out.println("onlineAStarGrid");
        for (int i=0; i<gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                System.out.print(onlineAStarGrid[i][j] + " ");
            }
            System.out.println();
        }*/

        while(true)
        {
            IVector2 agentCurrentPos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);

            if(MoveAction.getManhattanDistance(agentCurrentPos, emptyGridPosition) != 1){
                String newdir = getDirectionAccordingToOnlineAStar(agentCurrentPos, emptyGridPosition, onlineAStarGrid);
                onlineAStarGrid[agentCurrentPos.getXAsInteger()][agentCurrentPos.getYAsInteger()] += 1;
                // Perform move action.
                try
                {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
                    params.put(MoveAction.PARAMETER_DIRECTION, newdir);
                    Future<Void> fut = new Future<Void>();
                    env.performSpaceAction("move", params, new DelegationResultListener<Void>(fut));
                    fut.get();
                }
                catch(RuntimeException e)
                {
                }
            } else {
                break;
            }
        }
        // Spawn house
        Map houseProps = new HashMap();
        houseProps.put(Space2D.PROPERTY_POSITION, emptyGridPosition);
        env.createSpaceObject("house", houseProps, null);
        ResourceManager rm = ResourceManager.getInstance();
        rm.addResource(1, ResourceManager.HOUSING, 1);
        // Create new top-level goal.
        // IGoal grab_trees_goal = createGoal("grab_trees");
        // ispatchTopLevelGoal(grab_trees_goal);
    }

    private String getDirectionAccordingToOnlineAStar(IVector2 agentPos, IVector2 emptyGridPosition, int[][] onlineAStarGrid){
        String direction = MoveAction.DIRECTION_NONE;
        int x = agentPos.getXAsInteger();
        int y = agentPos.getYAsInteger();
        int bestX = x;
        int bestY = y;
        if(x > 0 && onlineAStarGrid[x-1][y] < onlineAStarGrid[bestX][y]){
            direction = MoveAction.DIRECTION_LEFT;
            bestX = x-1;
        }
        if(x < onlineAStarGrid.length-1 && onlineAStarGrid[x+1][y] < onlineAStarGrid[bestX][y]){
            direction = MoveAction.DIRECTION_RIGHT;
            bestX = x+1;
        }
        if(y > 0 && onlineAStarGrid[x][y-1] < onlineAStarGrid[x][bestY]){
            direction = MoveAction.DIRECTION_UP;
            bestY = y-1;
        }
        if(y < onlineAStarGrid.length-1 && onlineAStarGrid[x][y+1] < onlineAStarGrid[x][bestY]){
            direction = MoveAction.DIRECTION_DOWN;
        }
        return direction;
    }
}
