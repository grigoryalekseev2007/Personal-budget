package com.example.personalbudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private TextView balanceTextView, incomeTextView, expenseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setupClickListeners();
        updateSummary();
        loadTransactions();
    }

    private void initializeViews() {
        balanceTextView = findViewById(R.id.balanceTextView);
        incomeTextView = findViewById(R.id.incomeTextView);
        expenseTextView = findViewById(R.id.expenseTextView);
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);

        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        Button addTransactionBtn = findViewById(R.id.addTransactionBtn);
        Button viewCategoriesBtn = findViewById(R.id.viewCategoriesBtn);
        Button viewBudgetsBtn = findViewById(R.id.viewBudgetsBtn);

        addTransactionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        viewCategoriesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Category.class);
            startActivity(intent);
        });

        viewBudgetsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Budget.class);
            startActivity(intent);
        });
    }

    private void updateSummary() {
        double income = getTotalIncome();
        double expenses = getTotalExpenses();
        double balance = income - expenses;

        DecimalFormat df = new DecimalFormat("#,##0.00");

        balanceTextView.setText(df.format(balance) + " ₽");
        incomeTextView.setText("Доходы: " + df.format(income) + " ₽");
        expenseTextView.setText("Расходы: " + df.format(expenses) + " ₽");

        // Цвет баланса
        if (balance >= 0) {
            balanceTextView.setTextColor(getColor(android.R.color.holo_green_dark));
        } else {
            balanceTextView.setTextColor(getColor(android.R.color.holo_red_dark));
        }
    }

    private double getTotalIncome() {
        // Реализация подсчета доходов
        return 0.0; // Заглушка
    }

    private double getTotalExpenses() {
        // Реализация подсчета расходов
        return 0.0; // Заглушка
    }

    private void loadTransactions() {
        List<Transaction> transactions = dbHelper.getAllTransactions();
        transactionAdapter = new TransactionAdapter(transactions);
        transactionsRecyclerView.setAdapter(transactionAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();
        loadTransactions();
    }
}