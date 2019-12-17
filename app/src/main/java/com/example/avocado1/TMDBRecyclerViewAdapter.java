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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import com.example.avocado1.HomePage;


class TMDBRecyclerViewAdapter extends RecyclerView.Adapter<TMDBRecyclerViewAdapter.TMDBMovieHolder> {

    private static final String TAG = "TMDBRecyclerViewAdapter";
    private List<Movie> mMoviesList;
    private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private List<String> followingMovies;
    private ArrayList<Movie> movieArrayList;
    public HomePage homePageObject;


//    private void setHomePageObject(HomePage homePageObject){
//       // homePageObject.setOnTvShowsListener();
//        homePageObject.setOnMoviesListener();
//    }



    public TMDBRecyclerViewAdapter(Context context, List<Movie> moviesList) {
        mContext = context;
        this.mMoviesList = moviesList;
    }

    @NonNull
    @Override
    public TMDBMovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        //Called by the layout manager when it needs new view
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_movie_detail, parent, false);
        return new TMDBMovieHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TMDBMovieHolder holder, final int position) {
        // called by the layout manager when it wants new data in an existing row
        final Movie movieItem = mMoviesList.get(position);
        Log.d(TAG, "onBindViewHolder: " + movieItem.getTitle() + "==>" + position);
        Picasso.get().load(movieItem.getPoster_path())
                .error(R.drawable.movieimage)
                .placeholder(R.drawable.movieimage)
                .placeholder(R.drawable.movieimage)
                .into(holder.moviePoster);

        holder.title.setText(movieItem.getTitle());
        holder.overview.setText("תקציר:"+movieItem.getOverview());
        holder.voteAvg.setText("ציון ממוצע:"+String.valueOf(movieItem.getVote_average()));
        holder.releaseDate.setText("תאריך יציאה:"+movieItem.getRelease_date());




        holder.followBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateMoviesToDataBase(movieItem);
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called");
        return ((mMoviesList != null) && (mMoviesList.size() != 0) ? mMoviesList.size() : 0);
    }

    void loadNewData(List<Movie> newMovies) {
        mMoviesList = newMovies;
        notifyDataSetChanged();
    }


    static class TMDBMovieHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "TMDBMovieHolder";
        public LinearLayout newMoviesLinearLayout;


        ImageView moviePoster = null;
        TextView title = null;
        TextView overview = null;
        TextView voteAvg= null;
        TextView releaseDate= null;
        Button followBtn;



        public TMDBMovieHolder(@NonNull View itemView) {
            super(itemView);

            newMoviesLinearLayout = (LinearLayout) itemView.findViewById(R.id.contentmoviedetail);
            Log.d(TAG, "TMDBMovieHolder: starts");
            this.moviePoster = (ImageView) itemView.findViewById(R.id.moviePoster);
            this.title = (TextView) itemView.findViewById(R.id.movieTitle);
            this.overview = (TextView) itemView.findViewById(R.id.movieOverview);
            this.followBtn = (Button) itemView.findViewById(R.id.followBtn);
            this.voteAvg= (TextView) itemView.findViewById(R.id.movieVoteAvg);
            this.releaseDate= (TextView) itemView.findViewById(R.id.movieRelease_date);


        }


    }

    private void updateMoviesToDataBase(Movie movieItem) {
        myRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getDisplayName());
        myRef.child("followingMovies").child(movieItem.getId()).setValue(movieItem);



    }


}
