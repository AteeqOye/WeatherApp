package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    Context context ;
    ArrayList<WeatherRVModel> weatherRVModelArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModels) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModels;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (context).inflate (R.layout.weather_rv_items , parent , false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {

        WeatherRVModel model = weatherRVModelArrayList.get (position);
        holder.temperatureTV.setText (model.getTemperature ()+"°c");
        Picasso.get ().load ("http:".concat (model.getIcon ())).into (holder.conditionIV);
        holder.windSpeedTV.setText (model.getWindSpeed ()+"Km/h");

        SimpleDateFormat input = new SimpleDateFormat ("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat ("hh:mm aa");
        try{
            Date t = input.parse (model.getTime ());
            holder.timeTV.setText (output.format (t));
        }catch (ParseException e)
        {
            e.printStackTrace ();
        }



    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size ();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView windSpeedTV , temperatureTV , timeTV;
        ImageView conditionIV;

        public ViewHolder(@NonNull View itemView) {
            super (itemView);

            windSpeedTV = itemView.findViewById (R.id.TVWindSpeed);
            temperatureTV = itemView.findViewById (R.id.TVTemperature);
            timeTV = itemView.findViewById (R.id.TVTime);
            conditionIV = itemView.findViewById (R.id.TVCondition);

        }
    }
}
