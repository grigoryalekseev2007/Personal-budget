package com.example.personalbudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PersonalBudget.db";
    private static final int DATABASE_VERSION = 1;

    // Таблица категорий
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_CATEGORY_TYPE = "category_type"; // income/expense
    public static final String COLUMN_PARENT_ID = "parent_id";

    // Таблица операций
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COLUMN_TRANSACTION_ID = "transaction_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CATEGORY_ID_FK = "category_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_TYPE = "type"; // income/expense

    // Таблица бюджетов
    public static final String TABLE_BUDGETS = "budgets";
    public static final String COLUMN_BUDGET_ID = "budget_id";
    public static final String COLUMN_BUDGET_AMOUNT = "budget_amount";
    public static final String COLUMN_PERIOD = "period"; // monthly/weekly

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы категорий
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORY_NAME + " TEXT,"
                + COLUMN_CATEGORY_TYPE + " TEXT,"
                + COLUMN_PARENT_ID + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        // Создание таблицы операций
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_CATEGORY_ID_FK + " INTEGER,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_NOTE + " TEXT,"
                + COLUMN_TYPE + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_CATEGORY_ID_FK + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + ")"
                + ")";
        db.execSQL(CREATE_TRANSACTIONS_TABLE);

        // Создание таблицы бюджетов
        String CREATE_BUDGETS_TABLE = "CREATE TABLE " + TABLE_BUDGETS + "("
                + COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORY_ID_FK + " INTEGER,"
                + COLUMN_BUDGET_AMOUNT + " REAL,"
                + COLUMN_PERIOD + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_CATEGORY_ID_FK + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + ")"
                + ")";
        db.execSQL(CREATE_BUDGETS_TABLE);

        // Добавление предустановленных категорий
        initializeDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        onCreate(db);
    }

    private void initializeDefaultCategories(SQLiteDatabase db) {
        // Категории расходов
        String[] expenseCategories = {
                "Продукты", "Транспорт", "Жильё", "Развлечения", "Здоровье",
                "Одежда", "Образование", "Подарки", "Рестораны", "Прочее"
        };

        // Категории доходов
        String[] incomeCategories = {
                "Зарплата", "Фриланс", "Инвестиции", "Подарки", "Прочее"
        };

        ContentValues values = new ContentValues();

        // Добавляем категории расходов
        for (String category : expenseCategories) {
            values.put(COLUMN_CATEGORY_NAME, category);
            values.put(COLUMN_CATEGORY_TYPE, "expense");
            db.insert(TABLE_CATEGORIES, null, values);
        }

        // Добавляем категории доходов
        for (String category : incomeCategories) {
            values.put(COLUMN_CATEGORY_NAME, category);
            values.put(COLUMN_CATEGORY_TYPE, "income");
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    // Методы для работы с операциями
    public long addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_CATEGORY_ID_FK, transaction.getCategoryId());
        values.put(COLUMN_DATE, transaction.getDate());
        values.put(COLUMN_NOTE, transaction.getNote());
        values.put(COLUMN_TYPE, transaction.getType());

        return db.insert(TABLE_TRANSACTIONS, null, values);
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT t.*, c." + COLUMN_CATEGORY_NAME + " FROM " + TABLE_TRANSACTIONS + " t " +
                "LEFT JOIN " + TABLE_CATEGORIES + " c ON t." + COLUMN_CATEGORY_ID_FK + " = c." + COLUMN_CATEGORY_ID +
                " ORDER BY t." + COLUMN_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_ID)));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)));
                transaction.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID_FK)));
                transaction.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                transaction.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                transaction.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
                transaction.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    // Методы для работы с категориями
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                category.setType(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_TYPE)));
                category.setParentId(cursor.getInt(cursor.getColumnIndex(COLUMN_PARENT_ID)));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    // Методы для работы с бюджетами
    public long setBudget(Budget budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_ID_FK, budget.getCategoryId());
        values.put(COLUMN_BUDGET_AMOUNT, budget.getAmount());
        values.put(COLUMN_PERIOD, budget.getPeriod());

        return db.insert(TABLE_BUDGETS, null, values);
    }

    public double getTotalExpensesForCategory(int categoryId, String period) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COLUMN_CATEGORY_ID_FK + " = ? AND " + COLUMN_TYPE + " = 'expense'";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }
}