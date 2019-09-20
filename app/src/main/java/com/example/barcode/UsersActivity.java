package com.example.barcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class UsersActivity extends AppCompatActivity {

    DatabaseReference userRef;
    RecyclerView userRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        userRv = findViewById(R.id.userRv);
        userRv.setLayoutManager(new LinearLayoutManager(this));

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userRv();

    }

    private void userRv(){

        Query query = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        FirebaseRecyclerAdapter<User,UserHolder> adapter = new FirebaseRecyclerAdapter<User, UserHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserHolder holder, int i, @NonNull User model) {
                String key = getRef(i).getKey();

                holder.userIDTv.setText(model.getUserID());
                holder.nameTv.setText(model.getName());
                holder.branchTv.setText(model.getBranch());
                holder.collegeTv.setText(model.getCollege());
                holder.genderTv.setText(model.getGender());
                holder.rollNoTv.setText(model.getRollNumber());

                if (model.getVerify()!=null){
                    if (model.getVerify().equals("true")){
                        holder.verifyBt.setVisibility(View.GONE);
                    }
                }

                if(model.getIdImage()!=null){
                    Picasso.get().load(model.getIdImage()).into(holder.imageView);
                }

                holder.verifyBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userRef.child(key).child("verify").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(UsersActivity.this, "User Verified Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zunit_user,parent,false);
                return  new UserHolder(v);
            }
        };

        userRv.setAdapter(adapter);
        adapter.startListening();
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.nameTv)
        TextView nameTv;
        @BindView(R.id.rollNoTv)
        TextView rollNoTv;
        @BindView(R.id.branchTv)
        TextView branchTv;
        @BindView(R.id.userID)
        TextView userIDTv;
        @BindView(R.id.collegeTv)
        TextView collegeTv;
        @BindView(R.id.genderTv)
        TextView genderTv;
        @BindView(R.id.allowBt)
        TextView verifyBt;
        @BindView(R.id.imageView)
        ImageView imageView;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
