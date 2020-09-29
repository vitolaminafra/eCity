package com.vitolaminafra.ecity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupInfoActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private EditText nomeEdit, cognomeEdit;
    private TextView loadingText, subtitleText;

    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_info);

        nomeEdit = findViewById(R.id.nomeEdit);
        cognomeEdit = findViewById(R.id.cognoneEdit);
        loadingText = findViewById(R.id.loadingText);
        subtitleText = findViewById(R.id.textView);

        loggedUser = MainActivity.getLoggedUser();

        if(getIntent().hasCategory("edit")){
            nomeEdit.setText(loggedUser.getNome());
            cognomeEdit.setText(loggedUser.getCognome());
            subtitleText.setText("Modifica il tuo profilo");
        }

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();
    }

    public void continueSignup(View view) {
        String nome = nomeEdit.getText().toString();
        String cognome = cognomeEdit.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(!nome.equals("") && !cognome.equals("")) {
            loadingText.setVisibility(View.VISIBLE);
            if(currentUser != null) {
                String uid = currentUser.getUid();
                Map<String, Object> user = new HashMap<>();
                user.put("first", nome);
                user.put("last", cognome);

                if(getIntent().hasCategory("edit")){
                    db.collection("users").document(currentUser.getUid())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                    db.collection("users").document(currentUser.getUid())
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("ECITY", "DocumentSnapshot successfully written!");
                                    Toast.makeText(SignupInfoActivity.this, "Profilo modificato", Toast.LENGTH_SHORT).show();
                                    Intent mainIntent = new Intent(SignupInfoActivity.this, MainActivity.class);
                                    //mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(mainIntent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ECITY", "Error writing document", e);
                                    loadingText.setVisibility(View.INVISIBLE);
                                }
                            });

                }

                db.collection("users").document(currentUser.getUid())
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("ECITY", "DocumentSnapshot successfully written!");
                                Intent mainIntent = new Intent(SignupInfoActivity.this, SplashScreenActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainIntent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("ECITY", "Error writing document", e);
                                loadingText.setVisibility(View.INVISIBLE);
                            }
                        });

            }
        }

    }

}
