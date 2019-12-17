package com.example.avocado1;

import android.os.AsyncTask;
import android.util.Log;
import android.net.Uri;


import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class GetJSONDataByGenreTv extends AsyncTask<String, Void, List<TvShow>> implements GetRawData.OnDownloadComplete {

    private static final String TAG = "GetJSONDataByGenreTv";
    private List<TvShow> tvShowList = null;
    private String baseUrl;
    private String language;

    private final OnDataReadyTv callBack;
    // private final OnDataReadyTv callBacktv;
    private boolean runningOnSameThread = false;

    final private List<String> genresIdArray = new ArrayList<String>();

    interface OnDataReadyTv{
        void onDataReadyTv(List<TvShow> data, DownloadStatus status);
    }


    public GetJSONDataByGenreTv(OnDataReadyTv callBack, String baseUrl, String language) {
        Log.d(TAG, "GetJSONDataByGenreTv: called");
        this.baseUrl = baseUrl;
        this.language = language;
        this.callBack = callBack;
        // this.callBacktv= callBacktv;


    }

    void excuteOnSameThread(String searchCritiria) {
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

    private String createUri(String language) {

//        return Uri.parse(baseUrl).buildUpon()
//                .appendQueryParameter("lang", language).build().toString();
        return baseUrl;
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus downloadStatus) {
        Log.d(TAG, "onDownloadComplete status: " + downloadStatus);

        if (downloadStatus == DownloadStatus.OK) {
            tvShowList = new ArrayList<>();
            ArrayList<Integer> genres = new ArrayList<Integer>();

            try {

                JSONObject jData = new JSONObject(data);
                JSONArray resultsArray = jData.getJSONArray("results");


                for (int i = 0; i < resultsArray.length(); i++) {


                    JSONObject jMovie = resultsArray.getJSONObject(i);
                    String title = jMovie.getString("name");
                    String vote_avg = jMovie.getString("vote_average");
                    String overview = jMovie.getString("overview");
                    String release_date = jMovie.getString("first_air_date");
                    String poster = jMovie.getString("poster_path");
                    String id = jMovie.getString("id");
                    double popularity = jMovie.getDouble("popularity");


                    JSONObject genn = resultsArray.getJSONObject(i);
                    JSONArray genreIdArray = genn.getJSONArray("genre_ids");




                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Integer>>(){}.getType();
                    List<Integer> genereList = gson.fromJson(genreIdArray.toString(), type);

                    String posterLink = poster.replace("/", "https://image.tmdb.org/t/p/w92/");
                    // Discover size poster request ==>  https://image.tmdb.org/t/p/w92/fILTFOc4uV1mYL0qkoc3LyG1Jo9.jpg


                    TvShow tvShow = new TvShow(id, Double.parseDouble(vote_avg), title, popularity, posterLink, overview, release_date, (ArrayList<Integer>)genereList);
                    tvShowList.add(tvShow);





                    //  }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Error processing Json data " + e.getMessage());
                downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        //callback is only called if running on same thread
        if (runningOnSameThread && callBack != null) {
            //notify processing is done
            callBack.onDataReadyTv(tvShowList, downloadStatus);
        }

        Log.d(TAG, "onDownloadComplete: ends");

    }
}



