import java.util.*;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.*;

class Download extends Observable implements Runnable {
	
	public static String statuses[]={"Downloading","Paused","Completed","Cancel","Error"};
	
	public static int DOWNLOADING=0;
	public static int PAUSED=1;
	public static int COMPLETED=2;
	public static int CANCEL=3;
	public static int ERROR=4;
	
	private static int max_buffer_size=1024;
	 
	private int size;
	private int downloaded;
	private int status;
	private URL url;
	
	/*Initializes all the variables and starts download */
	
	public Download(URL u)
	{
		url=u;
		size=-1;
		downloaded=0;
		status=DOWNLOADING;
		download();
	}
	
	/*Returns the url in string form */ 
	
	public String getURL()
	{
		return url.toString();
	}
	
	/*Returns the size of the download */
	
	public int getSize()
	{
		return size;
	}
	
	/* Returns Download Progress as a percentage*/
	public float getProgress()
	{
		return ((float)downloaded/size)*100;
	}
	
	/* Returns the current download status */
	public int getStatus()
	{
		return status;
	}
	
	public void pause()
	{
		status=PAUSED;
		stateChanged();
	}
	
	public void resume()
	{
		status=DOWNLOADING;
		stateChanged();
		download();
	}
	
	public void cancel()
	{
		status=CANCEL;
		stateChanged();
	}
	
	public void error()
	{
		status=ERROR;
		stateChanged();
	}
	
	
	/*Creates a new Thread and starts the download*/
	private void download()
	{
		Thread t=new Thread(this);
		t.start();
	}
	
	
	private String getFileName(URL url)
	{
		String name=url.getFile();
		return name.substring(name.lastIndexOf('/')+1);
	}
	
	
	private void stateChanged()
	{
		setChanged();
		notifyObservers();
	}
	
	/* The main downloading occurs here */
	public void run()
	{
		InputStream stream=null;
		RandomAccessFile file=null;
		
		try
		{
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Range","bytes="+downloaded+"-");
			
			connection.connect();
			
			//Now check if the file to be downloaded is correct
			
			if(connection.getResponseCode()/100!=2)
			{
				System.out.println("1");
				error();
			}
			
			int length=connection.getContentLength();
			
			if(length<1)
			{
				System.out.println("2");
				error();
			}
			
			if(size==-1)
			{
				size=length;
				stateChanged();
			}
			
			file=new RandomAccessFile(getFileName(url),"rw");
			
			stream=connection.getInputStream();
			
			file.seek(downloaded);
			
			while(status==DOWNLOADING)
			{
				byte buffer[];
				
				if(size-downloaded<max_buffer_size)
				{
					buffer=new byte[size-downloaded];
				}
				else
				{
					buffer=new byte[max_buffer_size];
				}
				
				int r=stream.read(buffer);
				
				if(r==-1)
				{
					break;
				}
				
				file.write(buffer, 0, r);
				downloaded+=r;
				stateChanged();
			}
			
			if(status==DOWNLOADING)
			{
				status=COMPLETED;
				stateChanged();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			error();
		}
		finally
		{
			try
			{
				if(file!=null)
				{
					file.close();
				}
			}
			catch(Exception e)
			{
			}	
		}
		
		if(stream!=null)
		{
			try
			{
				stream.close();
			}
			catch(Exception e)
			{
			}
		}
	}
}
