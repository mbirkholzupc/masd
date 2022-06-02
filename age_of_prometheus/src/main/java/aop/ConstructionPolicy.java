package aop;

import java.util.Arrays;

public class ConstructionPolicy
{
    // Constants
    public static final int N_TEAMS = 6;  // Just a max value. Can be any number up to this.

    // Types
    public static final int HOUSE = 0;

    public class ConstructionRequest
    {
        public int building_type;
        public int x;
        public int y;
        public boolean in_progress;

        public ConstructionRequest(int _building_type, int _x, int _y)
        {
            building_type = _building_type;
            x = _x;
            y = _y;
            in_progress = false;
        }
    }

    // Note: Singleton using eager initialization.
    private static final ConstructionPolicy instance = new ConstructionPolicy();

    // One outstanding request per team
    private ConstructionRequest[] requests;
    private int[] resource_need;

    private ConstructionPolicy()
    {
        requests = new ConstructionRequest[N_TEAMS];
        resource_need = new int[N_TEAMS];
        for(int i=0; i<N_TEAMS; i++) {
            requests[i] = null;
            resource_need[i] = ResourceManager.NONE;
        }
    }

    public static ConstructionPolicy getInstance(){
        return instance;
    }

    public synchronized boolean submitRequest(int which_team, int building, int x, int y) {
        boolean rv = false;

        if(requests[which_team] == null)
        {
            requests[which_team] = new ConstructionRequest(building,x, y);
            rv = true;
        }

        return rv;
    }

    public synchronized ConstructionRequest getRequest(int which_team) {
        // Note: may return null, so need to check for null
        //   If the caller receives a non-null value, they must
        //   build the building where specified and then call 
        //   buildingComplete(), otherwise this process will get
        //   stuck (ok until we start killing agents)
        ConstructionRequest cr = requests[which_team];
        if(cr != null) {
            if (!cr.in_progress) {
                cr.in_progress = true;
            } else {
                cr = null;
            }
        }
        return cr;
    }

    public synchronized void buildingComplete(int which_team) {
        // Call this when building is complete. If it isn't cleared,
        // the czar cannot command more buildings to be created.
        requests[which_team] = null;
    }

    public synchronized void setResourceNeed(int which_team, int resource)
    {
        resource_need[which_team] = resource;
    }

    public synchronized int getResourceNeed(int which_team)
    {
        return resource_need[which_team];
    }
}
