package tsp.src.main.java.net.parasec.tsp;

import tsp.src.main.java.net.parasec.tsp.impl.Point;
import java.io.*;

public final class DumpPoints {
    public static void dump(final Point[] points, final String file) {
	
    	
    	for(int i = 0, len = points.length; i < len; i++)
		    System.out.println((points[i] + "\n").getBytes());
    	
    	/*try {
	    OutputStream os = null;
	    try {
		os = new BufferedOutputStream(new FileOutputStream(new File(file)));
		for(int i = 0, len = points.length; i < len; i++)
		    os.write((points[i] + "\n").getBytes());
		os.write((points[0] + "\n").getBytes());
	    } finally {
		if(os != null)
		    os.close();
	    }
	} catch(final IOException e) {
	    System.err.println(e);
	}*/
    }
}
