package ca.t10.blinddev.it.smartblindaddon;
//Chris Mutuc N01314607
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.HomeViewHolder> {
    ArrayList<HomeBlinds> testblinds;
    static Context context;
    TextView loc,light,temp;
    Button open,close;
    String location;
    public HomeRecyclerViewAdapter(ArrayList<HomeBlinds> test,Context context){
    this.testblinds = test;
    this.context = context;

    }


    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_recycleview,parent,false);
        loc = view.findViewById(R.id.home_rec_loc);
        light= view.findViewById(R.id.home_rec_light);
        temp = view.findViewById(R.id.home_rec_temp);
        open = view.findViewById(R.id.home_rec_open);
        close = view.findViewById(R.id.home_rec_close);
        applySettings();
        return new HomeViewHolder(view);
    }

    public void applySettings(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("saved", Context.MODE_PRIVATE);

        boolean d = sharedPreferences.getBoolean("dark",false);

        String t = sharedPreferences.getString("size","");

        if(d){enableDarkMode();}


        if (t.equals("large")){setTextSize(20);}
        if (t.equals("medium")){setTextSize(17);}
        if (t.equals("small")){setTextSize(13);}
    }
    public void setTextSize(int size){
        loc.setTextSize(size);
        light.setTextSize(size);
        temp.setTextSize(size);
        open.setTextSize(size);
        close.setTextSize(size);
    }
    private void enableDarkMode() {
        loc.setTextColor(context.getResources().getColor(R.color.white));
        light.setTextColor(context.getResources().getColor(R.color.white));
        temp.setTextColor(context.getResources().getColor(R.color.white));
    }
    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        HomeBlinds x = testblinds.get(position);
        String bkey = x.blindkey;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(bkey);

        ref.child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lo = snapshot.getValue(String.class);
                holder.loc.setText("Location: "+String.valueOf(lo));
                location = lo;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Temp error",error.toException());
            }
        });

        ref.child("Temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String t = snapshot.getValue(String.class);
                holder.temp.setText("Temperature: "+String.valueOf(t));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Temp error",error.toException());
            }
        });

        ref.child("Light").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String li  = snapshot.getValue(String.class);
                holder.light.setText("Light: "+String.valueOf(li));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Temp error",error.toException());
            }
        });





        //when open button is pressed status value on firebase = open
        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                x.openBlinds();
                Toast.makeText(view.getContext(),view.getContext().getString(R.string.open)  + location, Toast.LENGTH_SHORT).show();
            }
        });

        //when close button is pressed status value on firebase = close
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                x.closeBlinds();
                Toast.makeText(view.getContext(), view.getContext().getString(R.string.closing) + location, Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public int getItemCount() {
        return this.testblinds.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder{
        TextView loc,light,temp;
        Button open,close;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            loc = itemView.findViewById(R.id.home_rec_loc);
            open = itemView.findViewById(R.id.home_rec_open);
            light = itemView.findViewById(R.id.home_rec_light);
            temp = itemView.findViewById(R.id.home_rec_temp);
            close = itemView.findViewById(R.id.home_rec_close);
        }


    }

}
