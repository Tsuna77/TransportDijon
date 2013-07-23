package fr.tsuna.transportdijon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import android.os.Environment;
import android.util.Log;

public class myLog {
	private static Boolean debug=false;
	public static final String working_folder=Environment.getExternalStorageDirectory().toString()+"/diviaTotem/";
	public static final String log_file="divia.log";
	public static final String log_fullpath=working_folder+log_file;
	
	public static final int ERROR=0;
	public static final int WARNING=1;
	public static final int DEBUG=2;
	
	
	public static void clear(){
		// vidage du fichier de log
		File f = new File(log_fullpath);
		if (f.exists()){
			f.delete();
			try {
				f.createNewFile();
			} catch (IOException e) {
				debug = false;
				write("myLog",e.getMessage());
			}
		}
	}
	
	
	public static void write(String TAG, String message, int type){
		// Si le mode debug est actif
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.FRANCE);
				String date = simpleDateFormat.format(new Date()); 
				message = date+" "+message;
				if (debug){

					switch (type){
						case ERROR:
							Log.e(TAG, message);
							break;
						case WARNING:
							Log.w(TAG, message);
							break;
						case DEBUG:
							Log.d(TAG, message);
							break;
							
					}

					File logFile = new File(log_fullpath);
					   if (!logFile.exists())
					   {
					      try
					      {
					    	  File logFold = new File(working_folder);
					    	  logFold.mkdirs();
					    	  logFile.createNewFile();
					      } 
					      catch (IOException e)
					      {
					         e.printStackTrace();
					         debug = false;
					      }
					   }
					   try
					   {
					      //BufferedWriter for performance, true to set append to file flag
					      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
					      buf.append(message);
					      buf.newLine();
					      buf.close();
					   }
					   catch (IOException e)
					   {
					      myLog.write(TAG, e.getMessage(),myLog.ERROR);
					   }

				}
	}
	
	public static void write(String TAG,String message){
		write(TAG, message, DEBUG);
	}
}
