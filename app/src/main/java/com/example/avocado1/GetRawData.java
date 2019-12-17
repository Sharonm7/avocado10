package com.example.avocado1;

//This class will download JSON data from any URL

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK };

class GetRawData extends AsyncTask<String, Void, String> {

    private static final String TAG = "GetRawData";
    private DownloadStatus downloadStatus;
    private final OnDownloadComplete mCallBack;

    interface OnDownloadComplete{
        void onDownloadComplete(String data, DownloadStatus downloadStatus);
    }

    public GetRawData(OnDownloadComplete callBack) {
        downloadStatus = DownloadStatus.IDLE;
        mCallBack= callBack;
    }

    void runInSameThread(String s){

        Log.d(TAG, "runInSameThread:  starts");
        //onPostExecute(doInBackground(s)); implement callback
        if(mCallBack != null)
        {
            mCallBack.onDownloadComplete(doInBackground(s), downloadStatus);
        }
        Log.d(TAG, "runInSameThread: ends");

    }
    
    @Override
    protected void onPostExecute(String s) {
        //Log.d(TAG, "onPostExecute parameter = " + s);
        if (mCallBack!=null){
            mCallBack.onDownloadComplete(s, downloadStatus);
        }
        Log.d(TAG, "onPostExecute: ends");

    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection con = null;
        BufferedReader reader = null;


        if (strings == null)  {
            downloadStatus = DownloadStatus.NOT_INITIALISED;
            return null;
        }

        try {
            downloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int response= con.getResponseCode();
            Log.d(TAG, "doInBackground: Http Response was " + response);

            StringBuilder result = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

//            String line;
//            while (null !=(line = reader.readLine())){
//                result.append(line).append("\n");
//                downloadStatus = DownloadStatus.OK;
//                return  result.toString();
//            }
            for (String line = reader.readLine(); line!=null; line= reader.readLine()){
                result.append(line).append("\n");
            }

            downloadStatus = DownloadStatus.OK;
            return  result.toString();

        }catch(MalformedURLException e){
            Log.e(TAG, "doInBackground: Invalid URL" + e.getMessage() );
        }catch(IOException e){
            Log.e(TAG, "doInBackground: IO Exception reading data" + e.getMessage() );
        }catch (SecurityException e){
            Log.e(TAG, "doInBackground: Security Exception. Ned Permission?" + e.getMessage() );
        }
        finally {
            if (con!=null){
                con.disconnect();
            }
            if (reader!=null){
                try{
                    reader.close();
                }catch(IOException e){
                    Log.e(TAG, "doInBackground: Error closing stream" + e.getMessage() );
                }
            }
        }
        downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }
}
