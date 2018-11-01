/*
 * Copyright (C) 2013 Ricordeau Raphaël
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package fr.tsuna.transportdijon;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class myLog {
	private static Boolean debug=true;
	public static final String working_folder=Environment.getExternalStorageDirectory().toString()+"/TransportDijon/";
	public static final String log_file="divia.log";
	public static final String log_fullpath=working_folder+log_file;
	
	public static final int ERROR=0;
	public static final int WARNING=1;
	public static final int DEBUG=2;
	
	
	public static void clear(){
		// vidage du fichier de log
		/*File f = new File(log_fullpath);
		if (f.exists()){
			f.delete();
			try {
				f.createNewFile();
			} catch (IOException e) {
				debug = false;
				write("myLog",e.getMessage());
			}
		}*/
	}
	
	
	public static void write(String TAG, String message, int type){
		/*
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
				*/
	}
	
	public static void write(String TAG,String message){
		//write(TAG, message, DEBUG);
	}
}
