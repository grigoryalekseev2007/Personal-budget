package com.example.personalbudget;

public class Category {
    private int id;
    private String name;
    private String type;
    private int parentId;

    public Category() {}

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
}