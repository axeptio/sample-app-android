package com.davinciapp.samplejava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PreferencesAdapter extends RecyclerView.Adapter<PreferencesAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.preferences_item, parent, false);

        return new ViewHolder(view);
    }

    private PrefencesItemUI[] dataSet;

    PreferencesAdapter(PrefencesItemUI[] words) {
        dataSet = words;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(dataSet[position].title);
        holder.value.setText(dataSet[position].value);
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView value;

        public ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.textview_title);
            value = view.findViewById(R.id.textview_value);
        }

    }

}

class PrefencesItemUI {

    String title;
    String value;

    PrefencesItemUI(String title, String value) {
        this.title = title;
        this.value = value;
    }
}
