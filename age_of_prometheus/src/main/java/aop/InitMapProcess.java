package aop;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.lang.Math;

import aop.extern.OpenSimplexNoise;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

/**
 *  Environment process for creating wastes.
 */
// Could also implement something like IMap if there are constants we need
public class InitMapProcess extends SimplePropertyObject implements ISpaceProcess
{	
    /*
	public static Map imagenames;
	
	static
	{
		imagenames = new HashMap();
		imagenames.put("Oa", IMPENETRABLE_ROCK);	// Outer border
		
		imagenames.put("Ob", IMPENETRABLE_ROCK);
		imagenames.put("Oc", ROCK);
		imagenames.put("1B", REINFORCED_WALL);
		imagenames.put("Og", GOLD);
		imagenames.put("Oh", GEMS);
		
		imagenames.put("Od", DIRT_PATH);
		imagenames.put("1A", CLAIMED_PATH);
		imagenames.put("Oe", WATER);
		imagenames.put("Of", LAVA);
		
		imagenames.put("1G", DUNGEONHEART);
		imagenames.put("1C", TREASURY);
		imagenames.put("1F", HATCHERY);
		imagenames.put("1D", LAIR);
	}
    */
	
	//-------- attributes --------
	
	/** The last tick. */
	protected double lasttick;
	
	//-------- ISpaceProcess interface --------
	
	/**
	 *  This method will be executed by the object before the process gets added
	 *  to the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void start(IClockService clock, final IEnvironmentSpace space)
	{		
		// Initialize the map.
        System.out.println("Init process started.");
		
		try
		{
            System.out.println("Doing init process...");
			String mapfile = (String)getProperty("mapfile");
            //System.out.println("Parameter: " + mapfile);

			//final Space2D grid = (Space2D)space;
			final Grid2D grid = (Grid2D) space;

			OpenSimplexNoise noise = new OpenSimplexNoise(2);
			double noise_threshold = 0.5;

			/*
			// Note: This snippet doesn't seem to be compatible with the view. The area increases, but the view
			//       still shows what's in the xml file
            int sizex=40;
            int sizey=40;
            grid.setAreaSize(new Vector2Int(sizex, sizey));
            */

			//String type = (String)imagenames.get(key);
			String type = "tree";
			Map props = new HashMap();
			/*props.put(Space2D.PROPERTY_POSITION, new Vector2Int(5, 5));
			grid.createSpaceObject("tree", props, null);
//			    					System.out.println(x+" "+y+" "+type);

			props = new HashMap();
			props.put(Space2D.PROPERTY_POSITION, new Vector2Int(5, 6));
			grid.createSpaceObject("tree", props, null);*/

			int x_axis_length = grid.getAreaSize().getX().getAsInteger();
			int y_axis_length = grid.getAreaSize().getY().getAsInteger();

			for(int x=0; x<x_axis_length; x++)
			{
				for(int y=0; y<y_axis_length; y++)
				{
					props = new HashMap();
					props.put(Space2D.PROPERTY_POSITION, new Vector2Int(x, y));

					double stretch = 10;
					double xdbl = x/stretch;
					double ydbl = y/stretch;
					if(noise.eval(xdbl,ydbl) > noise_threshold)
					{
						grid.createSpaceObject("tree", props, null);
					}
				}
			}

			// And scatter a few random trees around too - could use Alessandro's code (below) to adjust
			// density for certain patches.
			for(int x=0; x<x_axis_length; x++){
				for(int y=0; y<y_axis_length; y++){
					double probability_threshold = 0.02;
					double random_number = Math.random();
					if(random_number < probability_threshold){
						Set<ISpaceObject> any_trees = grid.getNearObjects(new Vector2Int(x,y), new Vector1Double(0));
						if(any_trees.isEmpty()) {
							props = new HashMap();
							props.put(Space2D.PROPERTY_POSITION, new Vector2Int(x, y));
							props.put("wood", 100);
							grid.createSpaceObject("tree", props, null);
						}
					}
				}
			}

			// Wanted to try Alessandro's code below to create denser areas for apple trees, but crashed GUI
			// because of too many objects I guess...
			for(int x=0; x<x_axis_length; x++){
				for(int y=0; y<y_axis_length; y++){
					double probability_threshold = 0.07;
					double random_number = Math.random();
					if(random_number < probability_threshold){
						Set<ISpaceObject> any_objects = grid.getNearObjects(new Vector2Int(x,y), new Vector1Double(0));
						if(any_objects.isEmpty()) {
							props = new HashMap();
							props.put(Space2D.PROPERTY_POSITION, new Vector2Int(x, y));
							props.put("food", 200);
							grid.createSpaceObject("wildfood", props, null);
						}
						else
						{
							//System.out.println("Already something there!");
						}
					}
				}
			}


			/*
			for(double i=0; i<x_axis_length; i++){
				for(double j=0; j<y_axis_length; j++){
					double probability_decrease = 0.2;
					double random_number = Math.random() + probability_decrease;
					System.out.println(random_number + " " + (i/x_axis_length) + " " + (j/y_axis_length));
					if(random_number < (i/x_axis_length) && random_number < (j/y_axis_length)){
						props = new HashMap();
						props.put(Space2D.PROPERTY_POSITION, new Vector2Int((int)i, (int)j));
						grid.createSpaceObject("wildfood", props, null);
					}
				}
			}
			*/


			// Czars - create in position 1, 1 but they aren't actually displayed
			{
				props = new HashMap();
				props.put(Space2D.PROPERTY_POSITION, new Vector2Int((int)1, (int)1));
				grid.createSpaceObject("constructionczar", props, null);
				grid.createSpaceObject("economicczar", props, null);
			}



            /*
			final Space2D grid = (Space2D)space;
//			ClassLoader cl = space.getExternalAccess().getModel().getClassLoader();
			ClassLoader cl = getClass().getClassLoader();
			String mapfile = (String)getProperty("mapfile");
			InputStream is = SUtil.getResource(mapfile, cl);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
	
			// dis.available() returns 0 if the file does not have more lines.
			while(br.ready()) 
			{
		        String data = br.readLine();
		        
		        if("MAP".equals(data))
		        {
		        	String size = br.readLine();
		        	int del = size.indexOf("X");
		        	String xstr = size.substring(0, del-1);
		        	String ystr = size.substring(del+1);
		        	int sizex = Integer.parseInt(xstr.trim());
		        	int sizey = Integer.parseInt(ystr.trim());
		        	
		    		grid.setAreaSize(new Vector2Int(sizex, sizey));
		        
		    		// Now init the field
//		    		String line = br.readLine();
		    		for(int y=0; y<sizey; y++)
	    			{
		    			String	line = br.readLine();
	    				for(int x=0; x<sizex; x++)
	    				{
	    					String key = line.substring(x*2, x*2+2);
	    					String type = (String)imagenames.get(key);
	    					Map props = new HashMap();
	    					props.put("type", type);
	    					props.put(Space2D.PROPERTY_POSITION, new Vector2Int(x, y));
	    					grid.createSpaceObject("field", props, null);
//			    					System.out.println(x+" "+y+" "+type);
	    				}
	    			}
		        }
		        
		        if("CREATURES".equals(data))
		        {
		        	int cnt = Integer.parseInt(br.readLine().trim());
//				        	cnt = 1;
		        	for(int i=0; i<cnt; i++)
		        	{
		        		StringTokenizer stok = new StringTokenizer(br.readLine());
		        		while(stok.hasMoreTokens())
		        		{
		        			String type = stok.nextToken().toLowerCase();
		        			int x = Integer.parseInt(stok.nextToken());
		        			int y = Integer.parseInt(stok.nextToken());
		        			stok.nextToken(); //String level = stok.nextToken();
		        			stok.nextToken(); //String owner = stok.nextToken();
		        			
		        			HashMap props = new HashMap();
		        			props.put("type", type);
		        			props.put(Space2D.PROPERTY_POSITION, new Vector2Double(x, y));
		        			// todo: level, owner
		        			
		        			grid.createSpaceObject(type, props, null);
		        		}
		        	}
		        }
			}
			br.close();
            */
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			
		space.removeSpaceProcess(getProperty(ISpaceProcess.ID));
	
        System.out.println("Init process finished.");
	}
	
	/**
	 *  This method will be executed by the object before the process is removed
	 *  from the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space)
	{
//		System.out.println("create waste process shutdowned.");
	}

	/**
	 *  Executes the environment process
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
		System.out.println("process called: "+space);
	}
}
