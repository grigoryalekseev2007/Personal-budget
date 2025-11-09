package com.example.personalbudget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {
    private EditText amountEditText, noteEditText, dateEditText;
    private Spinner typeSpinner, categorySpinner;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();

        initializeViews();
        setupDatePicker();
        setupSpinners();
        setupSaveButton();
    }

    private void initializeViews() {
        amountEditText = findViewById(R.id.amountEditText);
        noteEditText = findViewById(R.id.noteEditText);
        dateEditText = findViewById(R.id.dateEditText);
        typeSpinner = findViewById(R.id.typeSpinner);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupDatePicker() {
        dateEditText.setOnClickListener(v -> showDatePicker());

        // Установка текущей даты по умолчанию
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateEditText.setText(sdf.format(calendar.getTime()));
    }

    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    dateEditText.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void setupSpinners() {
        // Спиннер типа операции
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        // Загрузка категорий
        loadCategories();
    }

    private void loadCategories() {
        List<Category> categories = dbHelper.getAllCategories();
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String amountStr = amountEditText.getText().toString();
        String note = noteEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String type = typeSpinner.getSelectedItem().toString();
        Category selectedCategory = (Category) categorySpinner.getSelectedItem();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Введите сумму", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setCategoryId(selectedCategory.getId());
        transaction.setDate(date);
        transaction.setNote(note);
        transaction.setType(type.equals("Доход") ? "income" : "expense");

        long result = dbHelper.addTransaction(transaction);

        if (result != -1) {
            Toast.makeText(this, "Операция добавлена", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show();
        }
    }
}