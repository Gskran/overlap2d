package com.uwsoft.editor.controlles;

import java.awt.EventQueue;
import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.uwsoft.editor.data.manager.DataManager;
import com.uwsoft.editor.utils.AppConfig;
import com.uwsoft.editor.utils.OSType;


public class CustomExceptionHandler implements UncaughtExceptionHandler {

    //private UncaughtExceptionHandler defaultUEH;
    private final static String sendURL = "http://overlap2d.com/error_report.php";
    
    /* 
     * if any of the parameters is null, the respective functionality 
     * will not be used 
     */
    public CustomExceptionHandler() {
        //this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
		String filename = "overlog.txt";
		writeToFile(stacktrace, filename);
        printWriter.close();

        sendError(stacktrace);

        showErrorDialog();
        //defaultUEH.uncaughtException(t, e);
    }
    
    public static void showErrorDialog(){
    	new Thread( new Runnable() {
    	      public void run() {
    	        EventQueue.invokeLater( new Runnable(){

					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, "Overlap2D just crashed, see stacktrace in overlog.txt file","Error",JOptionPane.ERROR_MESSAGE);
					}
    	        	
    	        });
    	      }
    	    } ).start();
    }
    
    public static void sendError(String stacktrace){
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("error", stacktrace);
		parameters.put("system", OSType.getOS_Type().toString());
		parameters.put("version", AppConfig.getInstance().version);
		HttpRequest httpGet = new HttpRequest(HttpMethods.GET);
		httpGet.setUrl(sendURL);
		httpGet.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		Gdx.net.sendHttpRequest (httpGet, new HttpResponseListener() {
			public void handleHttpResponse(HttpResponse httpResponse) {
				//showErrorDialog();
			}
			public void failed(Throwable t) {
				
			}
			@Override
			public void cancelled() {
				
			}
		 });


	}

    private void writeToFile(String stacktrace, String filename) {
        try {
            //String localPath = DataManager.getMyDocumentsLocation();
			String localPath = DataManager.getInstance().getRootPath();
            System.out.println(localPath +  File.separator + filename);
            BufferedWriter bos = new BufferedWriter(new FileWriter(localPath +  File.separator + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
