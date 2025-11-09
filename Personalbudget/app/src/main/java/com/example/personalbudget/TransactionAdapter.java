package com.example.personalbudget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        DecimalFormat df = new DecimalFormat("#,##0.00");

        holder.amountTextView.setText(df.format(transaction.getAmount()) + " ₽");
        holder.categoryTextView.setText(transaction.getCategoryName());
        holder.dateTextView.setText(transaction.getDate());
        holder.noteTextView.setText(transaction.getNote());

        // Цвет суммы в зависимости от типа операции
        if ("income".equals(transaction.getType())) {
            holder.amountTextView.setTextColor(holder.itemView.getContext()
                    .getColor(android.R.color.holo_green_dark));
        } else {
            holder.amountTextView.setTextColor(holder.itemView.getContext()
                    .getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView amountTextView;
        public TextView categoryTextView;
        public TextView dateTextView;
        public TextView noteTextView;

        public ViewHolder(View view) {
            super(view);
            amountTextView = view.findViewById(R.id.amountTextView);
            categoryTextView = view.findViewById(R.id.categoryTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            noteTextView = view.findViewById(R.id.noteTextView);
        }
    }
}