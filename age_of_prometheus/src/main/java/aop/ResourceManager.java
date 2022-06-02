package aop;

import java.util.Arrays;

// Threading notes
// Multi-threading synchronization handled through the use of the "synchronized" keyword on functions in the
// ResourceStash class. This prevents two agents from reading or writing at the same time, which could cause
// race conditions. Since each team has their own copy of a ResourceStash object, agents on different teams
// can access their resources at the same time from different threads. When resources are requested, it is
// imperative to check the return value since another agent could have decremented the resource pile in between
// when it was last checked and when resources were requested.

public class ResourceManager
{
    // Constants
    public static final int N_TEAMS = 6;  // Just a max value. Can be any number up to this.

    // Resource type constants
    public static final int FOOD=0;
    public static final int WOOD=1;
    public static final int ORE=2;
    public static final int HOUSING=3;
    public static final int POPULATION=4;
    public static final int N_RESOURCES=5;
    // Inner class to hold resource shares for each "team"
    public class ResourceStash
    {
        int[] resources;

        public ResourceStash() {
            resources = new int[N_RESOURCES];
            Arrays.fill(resources,0);  // Start out with 0 resources
        }

        public synchronized int readResource(int which_one) {
            int quantity = 0;
            if((which_one>=0)&&(which_one<N_RESOURCES)) {
                quantity = resources[which_one];
            }
            return quantity;
        }

        public synchronized void addResource(int which_one, int how_much) {
            if((which_one>=0)&&(which_one<N_RESOURCES)) {
                if(how_much>0) {
                    resources[which_one] += how_much;
                }
            }
        }

        public synchronized boolean requestResource(int which_one, int how_much) {
            boolean request_ok = false;
            if((which_one>=0)&&(which_one<N_RESOURCES)) {
                if( resources[which_one] >= how_much ) {
                    resources[which_one] -= how_much;
                    request_ok = true;
                }
            }
            return request_ok;
        }
    }

    // Note: Singleton using eager initialization.
    private static final ResourceManager instance = new ResourceManager();

    private ResourceStash[] stashes;

    private ResourceManager()
    {
        stashes = new ResourceStash[N_TEAMS];
        for(int i=0; i<N_TEAMS; i++) {
            stashes[i] = new ResourceStash();
        }
    }
    public static ResourceManager getInstance(){
        return instance;
    }

    public int readResource(int which_team, int which_one) {
        int quantity = 0;
        if ((which_team >= 0) && (which_team < N_TEAMS)) {
            quantity = stashes[which_team].readResource(which_one);
        }
        return quantity;
    }

    public void addResource(int which_team, int which_one, int how_much) {
        if ((which_team >= 0) && (which_team < N_TEAMS)) {
            stashes[which_team].addResource(which_one, how_much);

            String resources = "Resources: ";
            for (int i = 0; i < N_RESOURCES; i++) {
                resources += stashes[which_team].readResource(i) + " ";
            }
            System.out.println(resources);
        }
    }

    public void addHouse(int which_team) {
        if ((which_team >= 0) && (which_team < N_TEAMS)) {
            // Let's say a house can hold 5 people
            stashes[which_team].addResource(ResourceManager.HOUSING, 5);
        }
    }

    public boolean requestResource(int which_team, int which_one, int how_much) {
        boolean request_ok = false;
        if ((which_team >= 0) && (which_team < N_TEAMS)) {
            request_ok = stashes[which_team].requestResource(which_one, how_much);
        }
        return request_ok;
    }
    public boolean housingFull(int which_team) {
        boolean rv=false;
        if ((which_team >= 0) && (which_team < N_TEAMS)) {
            rv = stashes[which_team].readResource(HOUSING) <= stashes[which_team].readResource(POPULATION);
        }
        return rv;
    }
}