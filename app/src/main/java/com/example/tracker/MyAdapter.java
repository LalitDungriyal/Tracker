package com.example.tracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MyAdapter extends FirebaseRecyclerAdapter<RequestModel,MyAdapter.MyViewHolder> {

    Context context;
    public MyAdapter(@NonNull FirebaseRecyclerOptions<RequestModel> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i, @NonNull RequestModel requestModel) {
        RequestModel temp=requestModel;
        myViewHolder.email.setText(requestModel.getEmail());
        myViewHolder.fromadd.setText(requestModel.getFrom());
        myViewHolder.toadd.setText(requestModel.getTo());

        myViewHolder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("Going to deliver this courier?")
                        .setCancelable(true)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    HashMap<String ,Object> map=new HashMap();

                                    map.put("email",temp.getEmail());
                                    map.put("type",temp.getType());
                                    map.put("weight",temp.getWeight());
                                    map.put("from",temp.getFrom());
                                    map.put("to",temp.getTo());


                                    FirebaseDatabase.getInstance().getReference().child("OnGoing");

                                    FirebaseDatabase.getInstance().getReference().child("OnGoing").push()
                                            .setValue(map);

                                    FirebaseDatabase.getInstance().getReference().child("Couriers").child(getRef(i).getKey()).removeValue();

                                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED) {

                                        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email")
                                                .equalTo(temp.getEmail().toString());

                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                for (DataSnapshot data : snapshot.getChildren()) {
                                                    String number = String.valueOf(data.child("Number").getValue());

                                                    SmsManager smsManager = SmsManager.getDefault();
                                                    smsManager.sendTextMessage(number, null, "Your courier is on the way...you can track anytime..", null, null);
                                                    Toast.makeText(context, "sent message to " +data.child("Name").getValue(), Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    }

                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        });


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_design,parent,false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView email,fromadd,toadd;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            email=(TextView)itemView.findViewById(R.id.emailtext);
            fromadd=(TextView)itemView.findViewById(R.id.fromtext);
            toadd=(TextView)itemView.findViewById(R.id.totext);
        }
    }
}
