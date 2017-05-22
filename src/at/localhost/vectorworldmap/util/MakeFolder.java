package at.localhost.vectorworldmap.util;

import java.io.File;

public class MakeFolder {

	String root = "";
	public MakeFolder( String root ) {
		this.root = root;
	}

	public void makeSubFolder( String folder ) {
	    	String newFolder = root + folder;
	    	File theDir = new File( newFolder );
	    	if (!theDir.exists()) {
	    		System.out.println( "Creating [" + newFolder + "]");
	    		try {
	    			theDir.mkdir();
	    		} catch ( SecurityException e ) {
	    			System.out.println( "Failed to create [" + newFolder + "]" + e.getMessage() );
	    		}
	    	}
	    }

}
