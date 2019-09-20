package com.example.barcode;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VerifyActivity extends AppCompatActivity {

    EditText codeEt;
    DatabaseReference codeRef, issueRef, userRef, booksRef;
    String key, saveCurrentDate, returnDate;
    ProgressDialog loadingBar;
    @BindView(R.id.issueRl)
    RelativeLayout issueRl;
    String by;
    String shellScanner;
    String location;
    @BindView(R.id.bookNameTv)
    TextView bookNameTv;
    @BindView(R.id.publicationTv)
    TextView publicationTv;
    @BindView(R.id.authorTv)
    TextView authorTv;
    @BindView(R.id.locationTv)
    TextView locationTv;
    @BindView(R.id.statusTv)
    TextView statusTv;
    @BindView(R.id.verifyBt)
    Button verifyBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ButterKnife.bind(this);

        key = getIntent().getStringExtra("key");
        loadingBar = new ProgressDialog(this);
        shellScanner = getIntent().getStringExtra("shell");
        issueRl.setVisibility(View.GONE);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMMM , yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 14);
        returnDate = currentDate.format(cal.getTime());
        Log.d("Issue", "Date : " + returnDate);

        codeRef = FirebaseDatabase.getInstance().getReference().child("Issue").child(key);
        issueRef = FirebaseDatabase.getInstance().getReference().child("Issued Books").child(key);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        booksRef = FirebaseDatabase.getInstance().getReference().child("AllBooks").child(key);

        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.child("status").getValue().toString();
                    location = dataSnapshot.child("location").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String author = dataSnapshot.child("author").getValue().toString();
                    String publication = dataSnapshot.child("publication").getValue().toString();
                    locationTv.setText(location);

                    bookNameTv.setText(name);
                    authorTv.setText("Author : "  + author);
                    publicationTv.setText("Pub: " + publication);

                    if (location.equals(shellScanner)) {
                        if (status.equals("issued")) {
                            issueRl.setVisibility(View.GONE);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(VerifyActivity.this);
                            alertDialog.setTitle("Logout").setMessage("Do you want to return this book?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    returnBook();

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) { }

                            }).show();
                        } else {
                            issueRl.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(VerifyActivity.this, "Sorry ,you have scanned at wrong place!", Toast.LENGTH_SHORT).show();
                        if (shellScanner.equals("Shelve 1")) {
                            Intent intent = new Intent(VerifyActivity.this, ScannedBarCodeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(VerifyActivity.this, ShellActivity2.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                }else{

                    statusTv.setText("Sorry,this book is not available in database!");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        codeEt = findViewById(R.id.codeEt);

    }

    private void returnBook() {
        booksRef.child("status").setValue("available").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    codeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            by = dataSnapshot.child("by").getValue().toString();

                            return2();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    private void return2() {
        userRef.child(by).child("Issued Books").child(key).child("status").setValue("return").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    issueRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                codeRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Intent intent = new Intent(VerifyActivity.this, ScannedBarCodeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();

                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public void verify(View view) {

        String code = codeEt.getText().toString();
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.setMessage("Please wait");
        loadingBar.show();

        if (!TextUtils.isEmpty(code)){

            codeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("code")) {
                            String codeRec = dataSnapshot.child("code").getValue().toString();
                            String uid = dataSnapshot.child("by").getValue().toString();
                            String name = dataSnapshot.child("book").getValue().toString();
                            String shell = dataSnapshot.child("shell").getValue().toString();

                            if (shell.equals(shellScanner)) {

                                if (codeRec.equals(code)) {

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("issuedOn", saveCurrentDate);
                                    map.put("by", uid);
                                    map.put("returnDate", returnDate);
                                    map.put("key", key);

                                    issueRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("issuedOn", saveCurrentDate);
                                                map.put("returnDate", returnDate);
                                                map.put("status", "issued");
                                                map.put("key", key);
                                                map.put("name", name);

                                                userRef.child(uid).child("Issued Books").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            booksRef.child("status").setValue("issued").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        loadingBar.dismiss();
                                                                        Toast.makeText(VerifyActivity.this, "Congrats,you have issued your book!", Toast.LENGTH_SHORT).show();
                                                                        if (shellScanner.equals("Shelve 1")) {
                                                                            Intent intent = new Intent(VerifyActivity.this, ScannedBarCodeActivity.class);
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        } else {
                                                                            Intent intent = new Intent(VerifyActivity.this, ShellActivity2.class);
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }


                                                                    } else {

                                                                        loadingBar.dismiss();
                                                                        Toast.makeText(VerifyActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });
                                                        } else {

                                                            loadingBar.dismiss();
                                                            Toast.makeText(VerifyActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else{
                                    loadingBar.dismiss();
                                    Toast.makeText(VerifyActivity.this, "Wrong code Entered!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(VerifyActivity.this, "You are scanning it at wrong location Please scan it at its desired position!", Toast.LENGTH_SHORT).show();
                                if (shellScanner.equals("Shelve 1")) {

                                    Intent intent = new Intent(VerifyActivity.this, ScannedBarCodeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                } else {

                                    Intent intent = new Intent(VerifyActivity.this, ShellActivity2.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                }
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else{
            Toast.makeText(this, "Enter your code please!", Toast.LENGTH_SHORT).show();
        }
    }
}
