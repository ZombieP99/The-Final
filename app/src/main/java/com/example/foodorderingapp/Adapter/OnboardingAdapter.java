package com.example.foodorderingapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.R;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<OnboardingItem> onboardingItems;

    public OnboardingAdapter(List<OnboardingItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_onboarding, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.setOnboardingData(onboardingItems.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {

        private android.widget.ImageView imgOnboarding;
        private TextView titleTxt, descTxt;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgOnboarding = itemView.findViewById(R.id.imgOnboarding);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            descTxt = itemView.findViewById(R.id.descTxt);
        }

        void setOnboardingData(OnboardingItem item) {
            titleTxt.setText(item.getTitle());
            descTxt.setText(item.getDescription());
            imgOnboarding.setImageResource(item.getImageRes());
        }
    }

    public static class OnboardingItem {
        private String title;
        private String description;
        private int imageRes;

        public OnboardingItem(String title, String description, int imageRes) {
            this.title = title;
            this.description = description;
            this.imageRes = imageRes;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public int getImageRes() { return imageRes; }
    }
}
