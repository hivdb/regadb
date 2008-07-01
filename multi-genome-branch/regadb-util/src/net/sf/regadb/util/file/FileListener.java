package net.sf.regadb.util.file;

import java.io.File;
import java.io.RandomAccessFile;

public abstract class FileListener extends Thread {
	private boolean tailing;
	private File fileToListen;
	private int interval;
	
	public FileListener(File fileToListen, int interval) {
		this.fileToListen = fileToListen;
		this.interval = interval;
	}
	
	public void run() {
		long filePointer = 0;
		try
	    {
	      // Start tailing
	      this.tailing = true;
	      RandomAccessFile file = new RandomAccessFile( fileToListen, "r" );
	      while( this.tailing )
	      {
	        try
	        {  
	          // Compare the length of the file to the file pointer
	          long fileLength = this.fileToListen.length();
	          if( fileLength < filePointer ) 
	          {
	            // Log file must have been rotated or deleted; 
	            // reopen the file and reset the file pointer
	            file = new RandomAccessFile( fileToListen, "r" );
	            filePointer = 0;
	          }

	          if( fileLength > filePointer ) 
	          {
	            // There is data to read
	            file.seek( filePointer );
	            String line = file.readLine();
	            while( line != null && this.tailing)
	            {
	              newLineNotification( line );
	              line = file.readLine();
	            }
	            filePointer = file.getFilePointer();
	          }

	          // Sleep for the specified interval
	          sleep( this.interval );
	        }
	        catch( Exception e )
	        {
	        }
	      }

	      // Close the file that we are tailing
	      file.close();
	    }
	    catch( Exception e )
	    {
	      e.printStackTrace();
	    }
	}
	
	public void stopTailing() {
		this.tailing = false;
	}
	
	public abstract void newLineNotification(String line);
}
