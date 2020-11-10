package com.example.eorafinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class AdminHome extends AppCompatActivity {

    Button logoutBtn;
    ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_home);

        TextView emailshow = findViewById(R.id.getEmail);
        TextView uidshow = findViewById(R.id.getUid);

        //getFirebaseAuth
        auth = FirebaseAuth.getInstance();

        //getCurrentUser
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String nameFirebase = user.getDisplayName();
            String emailFirebase = user.getEmail();
            //boolean emailVerified = user.isEmailVerified();
            String uid = user.getUid();

            emailshow.setText(emailFirebase);
            uidshow.setText(uid);
        }

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    startActivity(new Intent(AdminHome.this, AdminLogin.class));
                    finish();
                }
            }
        };

        final CircularProgressButton upload = findViewById(R.id.logoutButton);
        final AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... strings) {
                for (int i = 0; i <= 100; i++){
                    publishProgress(i);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                return "yes";
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                upload.setProgress(values[0]);
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String s) {
                signOut();
                startActivity(new Intent(AdminHome.this, AdminLogin.class));
                finish();
                super.onPostExecute(s);
            }
        };

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminHome.this);
                builder.setMessage("ANDA INGIN LOGOUT?")
                        .setCancelable(false)
                        .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                upload.startMorphAnimation();
                                task.execute();
                            }
                        })
                        .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void signOut() {
        auth.signOut();
    }

    private void LogoutAsking() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ANDA INGIN LOGOUT?")
                .setCancelable(false)
                .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(AdminHome.this, AdminLogin.class));
                        signOut();
                        finish();
                    }
                })
                .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Tidak diizinkan!", Toast.LENGTH_SHORT).show();
    }
}
