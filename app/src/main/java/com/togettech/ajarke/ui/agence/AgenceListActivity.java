package com.togettech.ajarke.ui.agence;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.togettech.ajarke.R;
import com.togettech.ajarke.ui.agence.adapter.AgenceAdapter;
import com.togettech.ajarke.ui.agence.model.Agence;

import java.util.ArrayList;
import java.util.List;

public class AgenceListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AgenceAdapter agenceAdapter;
    private List<Agence> agenceList;
    ProgressBar loader;
    EditText agence_reseach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agence);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view_agence);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        agenceList = new ArrayList<>();

        loader = findViewById(R.id.loader);
        
        readAgences();

        agence_reseach = findViewById(R.id.agence_reseach);
        agence_reseach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                agenceReseach(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

    }

    private void readAgences() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Agences_voyages");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                agenceList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Agence client = snapshot.getValue(Agence.class);
                    agenceList.add(client);
                }

                agenceAdapter = new AgenceAdapter(getApplicationContext(), agenceList, false);
                recyclerView.setAdapter(agenceAdapter);
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void agenceReseach(String s){
        final FirebaseUser fagence = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Agences_voyages").orderByChild("agence_name")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                agenceList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Agence agence = snapshot.getValue(Agence.class);

                    agenceList.add(agence);

                }
                agenceAdapter = new AgenceAdapter(AgenceListActivity.this, agenceList, false);
                recyclerView.setAdapter(agenceAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
