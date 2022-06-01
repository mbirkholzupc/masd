package aop;

import java.util.Arrays;

public class EconomicPolicy
{
    // Constants
    public static final int N_TEAMS = 6;  // Just a max value. Can be any number up to this.

    // Policy type constants
    public static final int STATERUN=0;
    public static final int FREEMARKET=1;

    // Resource need constants
    public static final int FOOD=0;
    public static final int WOOD=1;
    public static final int ORE=2;
    public static final int N_RESOURCES=3;

    public class NeededResource
    {
        int current_demanded_resource;

        public NeededResource() {
            current_demanded_resource = FOOD;
        }

        public synchronized int readResource() {
            return current_demanded_resource;
        }

        public synchronized void setResource(int which_one) {
            if((which_one>=0)&&(which_one<N_RESOURCES)) {
                current_demanded_resource = which_one;
            }
        }
    }

    // Note: Singleton using eager initialization.
    private static final EconomicPolicy instance = new EconomicPolicy();

    private NeededResource[] needs;

    private EconomicPolicy()
    {
        needs = new NeededResource[N_TEAMS];
        for(int i=0; i<N_TEAMS; i++) {
            needs[i] = new NeededResource();
        }
    }

    public static EconomicPolicy getInstance(){
        return instance;
    }

    public int readPolicy(int which_team) {
        // Always state-run for now
        return STATERUN;
    }

    public int readNeededResource(int which_team) {
        return needs[which_team].readResource();
    }

    public void setNeededResource(int which_team, int which_one) {
        needs[which_team].setResource(which_one);
    }
}
