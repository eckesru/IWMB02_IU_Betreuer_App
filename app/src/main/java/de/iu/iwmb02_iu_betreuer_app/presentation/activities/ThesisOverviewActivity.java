package de.iu.iwmb02_iu_betreuer_app.presentation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import de.iu.iwmb02_iu_betreuer_app.R;
import de.iu.iwmb02_iu_betreuer_app.data.dao.FirebaseFirestoreDao;
import de.iu.iwmb02_iu_betreuer_app.model.Thesis;
import de.iu.iwmb02_iu_betreuer_app.model.User;
import de.iu.iwmb02_iu_betreuer_app.presentation.adapters.ThesisRecyclerAdapter;

public class ThesisOverviewActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    private static final String TAG = "ThesisOverviewActivity";
    private final Context context = ThesisOverviewActivity.this;
    private FirebaseAuth auth;
    private FirebaseFirestoreDao firebaseFirestoreDao;
    private RecyclerView thesisOverviewRecyclerView;
    private ThesisRecyclerAdapter thesisRecyclerAdapter;
    private TextView txtHiUser;
    private User user;
    private MaterialToolbar toolbar;

    private ConstraintLayout chipGroups;
    Chip primaryChip;
    Chip secondaryChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thesis_overview);

        auth = FirebaseAuth.getInstance();
        firebaseFirestoreDao = FirebaseFirestoreDao.getInstance();

        thesisOverviewRecyclerView = findViewById(R.id.thesisOverviewRecyclerView);
        txtHiUser = findViewById(R.id.hiUserNameTextView);
        toolbar = findViewById(R.id.materialToolbar);

        thesisRecyclerAdapter = getSupervisorRecyclerAdapter("");
        thesisOverviewRecyclerView.setAdapter(thesisRecyclerAdapter);
        thesisOverviewRecyclerView.setItemAnimator(null);

        initializeChips();

        chipGroups = findViewById(R.id.chipFilterConstraintLayout);
        setDefaultFilters();

        toolbar = findViewById(R.id.materialToolbar);
        setOnClickListeners(toolbar);

        handleUserGreeting();
    }


    private void initializeChips() {
        primaryChip = findViewById(R.id.chipPrimarySupervisor);
        secondaryChip = findViewById(R.id.chipSecondarySupervisor);
        //TODO: Initialize all chips
    }

    private void setDefaultFilters() {
        primaryChip.setChecked(true);
// TODO: Set all default filters
    }

    public void setOnClickListeners(MaterialToolbar toolbar) {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menuItem_filter) {
                    if (chipGroups.getVisibility() == View.VISIBLE) {
                        final Animation slideOutRight = AnimationUtils.loadAnimation(ThesisOverviewActivity.this, android.R.anim.slide_out_right);
                        slideOutRight.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            public void onAnimationEnd(Animation animation) {
                                chipGroups.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }

                        });
                        chipGroups.startAnimation(slideOutRight);
                    } else {
                        chipGroups.startAnimation(AnimationUtils.loadAnimation(ThesisOverviewActivity.this, android.R.anim.slide_in_left));
                        chipGroups.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void handleUserGreeting() {
        Intent intent = getIntent();
        if(intent.hasExtra("user")){
            user = (User) intent.getSerializableExtra("user");
            txtHiUser.setText(user.getNameFirst());
            return;
        }
        txtHiUser.setText("User");
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(this);
        thesisRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        thesisRecyclerAdapter.stopListening();
        auth.removeAuthStateListener(this);
    }

    private ThesisRecyclerAdapter getSupervisorRecyclerAdapter(String filterOption){
        FirestoreRecyclerOptions<Thesis> options = getFirestoreRecyclerOptions(getThesisQuery(filterOption));
        return new ThesisRecyclerAdapter(options);
    }

    private FirestoreRecyclerOptions<Thesis> getFirestoreRecyclerOptions(Query query){
        return new FirestoreRecyclerOptions.Builder<Thesis>()
                .setQuery(query, Thesis.class)
                .build();
    }

    private Query getThesisQuery(String filterOption){
        Query query = firebaseFirestoreDao.getThesesCollectionRef();

        return query;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            Log.d(TAG, "onAuthStateChanged: No user signed in. Starting LoginActivity");
            ActivityStarter.startLoginActivity(context);
            this.finish();
        }

    }

    private void logOutUser(){
        Log.d(TAG, "logOutUser: Logging out");
        AuthUI.getInstance().signOut(this);
        Toast.makeText(context, getString(R.string.logged_out), Toast.LENGTH_SHORT).show();
        this.finish();
    }

}