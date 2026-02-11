package com.example.foodorderingapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.Model.Food;
import com.example.foodorderingapp.Model.Order;
import com.example.foodorderingapp.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<Order> orders;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        
        holder.orderId.setText("Order #" + (order.getId() != null ? order.getId().substring(0, Math.min(order.getId().length(), 8)) : "N/A"));
        holder.status.setText(order.getStatus());
        
        if (order.getOrderDate() != null) {
            holder.date.setText(dateFormat.format(order.getOrderDate().toDate()));
        }

        StringBuilder itemsStr = new StringBuilder();
        if (order.getItems() != null) {
            for (int i = 0; i < order.getItems().size(); i++) {
                Food item = order.getItems().get(i);
                itemsStr.append(item.getQuantity()).append("x ").append(item.getName());
                if (i < order.getItems().size() - 1) {
                    itemsStr.append(", ");
                }
            }
        }
        holder.items.setText(itemsStr.toString());
        holder.total.setText("$" + String.format(Locale.getDefault(), "%.2f", order.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, status, date, items, total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            status = itemView.findViewById(R.id.orderStatus);
            date = itemView.findViewById(R.id.orderDate);
            items = itemView.findViewById(R.id.orderItems);
            total = itemView.findViewById(R.id.orderTotal);
        }
    }
}
