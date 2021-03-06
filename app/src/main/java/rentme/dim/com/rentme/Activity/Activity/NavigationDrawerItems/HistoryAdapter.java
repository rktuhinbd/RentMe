package rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import rentme.dim.com.rentme.Activity.Data.Requests;
import rentme.dim.com.rentme.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private ArrayList<Requests> tHistoryList;

    private OnItemClickListener tListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        tListener = listener;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{

        public TextView hDateTime;
        public TextView hSource;
        public TextView hDestination;
        public TextView hCarType;
        public TextView hCost;
        public ImageView mDeleteItem;

        public HistoryViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            mDeleteItem = itemView.findViewById(R.id.imageView_delete);
            hDateTime = itemView.findViewById(R.id.textView_date);
            hSource = itemView.findViewById(R.id.textView_source);
            hDestination = itemView.findViewById(R.id.textView_destination);
            hCarType = itemView.findViewById(R.id.textView_car_type);
            hCost = itemView.findViewById(R.id.textView_cost);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public HistoryAdapter(ArrayList<Requests> historyLists){
        tHistoryList = historyLists;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_list, parent, false);
        HistoryViewHolder viewHolder = new HistoryViewHolder(v, tListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Requests currentItem = tHistoryList.get(position);

        String[] DateTime = currentItem.getRequestDate().split("\\s+");
        String[] Time = DateTime[1].split(":");
        int Hour = Integer.parseInt(Time[0]);
        String time;

        if(HoursDifference(currentItem) < 0){
            holder.mDeleteItem.setVisibility(View.GONE);
        }

        if(Hour > 12){
            Hour = Hour - 12;
            time = Hour + ":" + Time[1] + " PM";
        }
        else if (Hour == 12){
            time = Hour + ":" + Time[1] + " PM";
        }
        else{
            time = Hour + ":" + Time[1] + " AM";
        }

        String FinalDateTime = DateTime[0] + "\n" + time;

        holder.hDateTime.setText(FinalDateTime);
        holder.hSource.setText(currentItem.getStartingPoint());
        holder.hDestination.setText(currentItem.getEndingPoint());
        holder.hCarType.setText(currentItem.getCarType());
        holder.hCost.setText(currentItem.getRentCost());
    }


    private long HoursDifference(Requests requestItem){
        long hoursDiff = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm");
        try{
            TimeZone timeZone = TimeZone.getTimeZone("GMT+6");
            Calendar calendar = Calendar.getInstance(timeZone);

            Date currentDate = simpleDateFormat.parse(calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
            Date requestedDate = simpleDateFormat.parse(requestItem.getRequestDate());

            long differenceBetweenDates = requestedDate.getTime() - currentDate.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            hoursDiff = differenceBetweenDates / hoursInMilli;
            differenceBetweenDates = differenceBetweenDates % hoursInMilli;

            //Log.e("Hours Differenc",""+hoursDiff+"-----"+differenceBetweenDates);
        }
        catch(Exception e){}
        return hoursDiff;
    }

    @Override
    public int getItemCount() {
        return tHistoryList.size();
    }
}