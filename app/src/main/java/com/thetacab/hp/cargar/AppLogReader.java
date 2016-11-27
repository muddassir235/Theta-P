package com.thetacab.hp.cargar;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by gul on 8/8/16.
 */
public class AppLogReader {
    private static final String TAG = AppLogReader.class.getCanonicalName();
    private static final String processId = Integer.toString(android.os.Process
            .myPid());
    private TextView text;
    private Context context;
    public AppLogReader(Context context){
        this.context=context;
    }
    public StringBuilder getLog() {

        StringBuilder builder = new StringBuilder();

        try {
            String[] command = new String[] { "logcat", "-v", "threadtime" };

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(processId)) {
                    //text.setText(line);
                    writeToFile(line);



                }
            }
        } catch (IOException ex) {
            Log.e(TAG, "getLog failed", ex);
        }

        return builder;
    }
    public void writeToFile(String data) {
        try {


            FileOutputStream fOut = context.openFileOutput("User-Cargar-Logs.txt",
                    context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException E){
            E.printStackTrace();
        }
    }

    public void readFromFile(TextView get) {

        String ret = "";
        String input="";

        try {
            InputStream inputStream = context.openFileInput("Cargar-Logs.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);

                }

                inputStream.close();
                ret = stringBuilder.toString();
                Log.e("ret",ret);
                get.setText(ret);

            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }


    }

}
