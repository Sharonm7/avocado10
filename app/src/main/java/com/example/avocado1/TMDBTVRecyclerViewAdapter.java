package com.example.avocado1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


import androidx.recyclerview.widget.RecyclerView;

class TMDBTVRecyclerViewAdapter extends RecyclerView.Adapter<TMDBTVRecyclerViewAdapter.TMDBTVMovieHolder>{


    private static final String TAG = "TMDBTVRecyclerViewAdapter";
    private List<TvShow> mTvShowList;
    private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;


    public TMDBTVRecyclerViewAdapter(Context context, List<TvShow> tvShowList) {
        mContext = context;
        this.mTvShowList = tvShowList;
    }

    @NonNull
    @Override
    public TMDBTVRecyclerViewAdapter.TMDBTVMovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        //Called by the layout manager when it needs new view
     //  Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_tvshow_detail, parent, false);
        return new TMDBTVRecyclerViewAdapter.TMDBTVMovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TMDBTVRecyclerViewAdapter.TMDBTVMovieHolder holder, final int position) {
        // called by the layout manager when it wants new data in an existing row
        final TvShow tvShowItem = mTvShowList.get(position);
        //Log.d(TAG, "onBindViewHolder: " + tvShowItem.getTitle() + "==>" + position);
       Picasso.get().load(tvShowItem.getPoster_path())
                .error(R.drawable.movieimage)
                .placeholder(R.drawable.movieimage)
                .placeholder(R.drawable.movieimage)
                .into(holder.tvshowPoster);

        holder.title.setText(tvShowItem.getTitle());
        holder.overview.setText(tvShowItem.getOverview());
        holder.tvvoteAvg.setText("ציון ממוצע:"+String.valueOf(tvShowItem.getVote_average()));
        holder.tvreleaseDate.setText("תאריך יציאה:"+tvShowItem.getRelease_date());
/*
        holder.moviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "You Clicked the poster", Toast.LENGTH_LONG).show();
            }
        });


        holder.newMoviesLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mContext, "You Clicked the layout ", Toast.LENGTH_LONG).show();
            }
        });
*/
        holder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTvShowsToDataBase(tvShowItem);

            }
        });



    }



    @Override
    public int getItemCount() {
      //  Log.d(TAG, "getItemCount: called");
        return ((mTvShowList != null) && (mTvShowList.size() != 0) ? mTvShowList.size() : 0);
    }

    void loadNewData(List<TvShow> newTvShows){
        mTvShowList = newTvShows;
        notifyDataSetChanged();
    }

//    public Movie getMovieIntoPosition{
//        return ((mMoviesList != null) && (mMoviesList.size() != 0) ? mMoviesList.get(position) : null);
//    }

    static class TMDBTVMovieHolder extends RecyclerView.ViewHolder{

        private static final String TAG = "TMDBMovieHolder";
        public LinearLayout newTvShowsLinearLayout;


        ImageView tvshowPoster = null;
        TextView title = null;
        TextView overview= null;
        Button followBtn;
        TextView tvvoteAvg= null;
        TextView tvreleaseDate= null;


        //TextView overview = null;

        public TMDBTVMovieHolder(@NonNull View itemView) {
            super(itemView);

            newTvShowsLinearLayout= (LinearLayout) itemView.findViewById(R.id.contenttvshowdetail);
            Log.d(TAG, "TMDBMovieHolder: starts");
            this.tvshowPoster = (ImageView)itemView.findViewById(R.id.tvshowPoster);
            this.title = (TextView)itemView.findViewById(R.id.tvshowTitle);
            this.overview = (TextView)itemView.findViewById(R.id.tvshowOverview);
            this.followBtn= (Button) itemView.findViewById(R.id.followBtn);
            this.tvvoteAvg= (TextView) itemView.findViewById(R.id.tvVoteAvg);
            this.tvreleaseDate= (TextView) itemView.findViewById(R.id.tvRelease_date);


        }


    }

    private void updateTvShowsToDataBase(TvShow tvShowItem) {

        myRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getDisplayName());
        myRef.child("followingTvShows").child(tvShowItem.getId()).setValue(tvShowItem);

    }
}

