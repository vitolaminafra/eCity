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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "ECITY";

    private FirebaseAuth mAuth;

    private EditText emailEdit;
    private EditText pswEdit;

    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEdit = findViewById(R.id.emailLogin);
        pswEdit = findViewById(R.id.pswLogin);
        loadingText = findViewById(R.id.loadingText);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

    }


    public void login(View view) {
        String email = emailEdit.getText().toString();
        String psw = pswEdit.getText().toString();
         if(!email.equals("") && !psw.equals("")) {
             loadingText.setVisibility(View.VISIBLE);
             mAuth.signInWithEmailAndPassword(email, psw)
                     .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful()) {
                                 Log.d(TAG, "signInWithEmail:success");
                                 Toast.makeText(LoginActivity.this, "Accesso eseguito",
                                         Toast.LENGTH_SHORT).show();

                                 Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                 mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                 startActivity(mainIntent);


                             } else {
                                 loadingText.setVisibility(View.INVISIBLE);
                                 Log.w(TAG, "signInWithEmail:failure", task.getException());
                                 Toast.makeText(LoginActivity.this, "Accesso fallito",
                                         Toast.LENGTH_SHORT).show();

                             }
                         }
                     });
         }

    }

    public void signup(View view) {
        String email = emailEdit.getText().toString();
        String psw = pswEdit.getText().toString();

        if(!email.equals("") && !psw.equals("")) {
            loadingText.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, psw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                Toast.makeText(LoginActivity.this, "Registrazione effettuata",
                                        Toast.LENGTH_SHORT).show();

                                Intent infoIntent = new Intent(LoginActivity.this, SignupInfoActivity.class);
                                infoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(infoIntent);

                            } else {
                                loadingText.setVisibility(View.INVISIBLE);
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Registrazione fallita",
                                        Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }
    }
}
