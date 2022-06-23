package ca.t10.blinddev.it.smartblindaddon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.HomeViewHolder> {
    ArrayList<HomeBlinds> testblinds;
    public HomeRecyclerViewAdapter(ArrayList<HomeBlinds> test){
    this.testblinds = test;
    }


    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_recycleview,parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        HomeBlinds x = testblinds.get(position);
        holder.text.setText(x.getString());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Test", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.testblinds.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        Button button;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.home_rec_text);
            button = itemView.findViewById(R.id.home_rec_button);
        }
    }
}
