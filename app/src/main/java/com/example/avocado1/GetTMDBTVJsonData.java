package com.example.avocado1;

import android.os.AsyncTask;
import android.util.Log;
import android.net.Uri;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetTMDBTVJsonData extends AsyncTask<String, Void, List<TvShow>> implements GetRawData.OnDownloadComplete {

    private static final String TAG = "GetTMDBJsonData";
    private List<TvShow> tvShowList = null;
    private String baseUrl;
    private String language;

    private final OnDataReadyTv callBack;
    private boolean runningOnSameThread = false;



    interface OnDataReadyTv{
        void onDataReadyTv(List<TvShow> data, DownloadStatus status);
    }


    public GetTMDBTVJsonData(OnDataReadyTv callBack , String baseUrl, String language) {
        Log.d(TAG, "GetTMDBJsonData: called");
        this.baseUrl = baseUrl;
        this.language = language;
        this.callBack = callBack;


    }

    void excuteOnSameThread(String searchCritiria){
        Log.d(TAG, "excuteOnSameThread: starts");
        runningOnSameThread = true;
        String destUri = createUri(language);

        GetRawData grd = new GetRawData(this);
        grd.execute(destUri);
        Log.d(TAG, "excuteOnSameThread: ends");
    }

    @Override
    protected void onPostExecute(List<TvShow> tvShows) {
        Log.d(TAG, "onPostExecute: starts");
        if(callBack != null){
            callBack.onDataReadyTv(tvShowList, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<TvShow> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: starts");
        String destURII = createUri(language);

        GetRawData grd = new GetRawData(this);
        grd.runInSameThread(destURII);
        Log.d(TAG, "doInBackground: ends");

        return tvShowList;
    }

    private String createUri(String language){

//        return Uri.parse(baseUrl).buildUpon()
//                .appendQueryParameter("lang", language).build().toString();
        return baseUrl;
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus downloadStatus) {
        Log.d(TAG, "onDownloadComplete status: "+ downloadStatus);

        if(downloadStatus == DownloadStatus.OK){
            tvShowList = new ArrayList<>();
            ArrayList <Integer> genres= new ArrayList<Integer>();
            try{
                JSONObject jData = new JSONObject(data);
                JSONArray resultsArray = jData.getJSONArray("results");

                for(int i=0; i<resultsArray.length(); i++){
                    genres.clear();


                    JSONObject jMovie = resultsArray.getJSONObject(i);
                    String title = jMovie.getString("name");
                    String vote_avg = jMovie.getString("vote_average");
                    String overview = jMovie.getString("overview");
                   // String trailer = jMovie.getString("video");
                    String release_date = jMovie.getString("first_air_date");
                    String poster = jMovie.getString("poster_path");
                    String id = jMovie.getString("id");
                    double popularity = jMovie.getDouble("popularity");



                    JSONObject genn = resultsArray.getJSONObject(i);
                    JSONArray genreIdArray = genn.getJSONArray("genre_ids");


                    for(int j=0; j<genreIdArray.length(); j++){
                        genres.add(genreIdArray.getInt(j));

                    }


                    String posterLink = poster.replace("/", "https://image.tmdb.org/t/p/w92/");
                    // Example of Lion King poster request ==>  https://image.tmdb.org/t/p/original/fILTFOc4uV1mYL0qkoc3LyG1Jo9.jpg
                    // Discover size poster request ==>  https://image.tmdb.org/t/p/w92/fILTFOc4uV1mYL0qkoc3LyG1Jo9.jpg

                    TvShow tvShow = new TvShow(id, Double.parseDouble(vote_avg), title ,popularity, posterLink, overview, release_date, genres);
                    tvShowList.add(tvShow);

                    Log.d(TAG, "onDownloadComplete: " + tvShow.toString());
                }
            }catch (JSONException e){
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Error processing Json data " + e.getMessage() );
                downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        //callback is only called if running on same thread
        if (runningOnSameThread && callBack != null){
            //notify processing is done
            callBack.onDataReadyTv(tvShowList, downloadStatus);
        }

        Log.d(TAG, "onDownloadComplete: ends");

    }
}
