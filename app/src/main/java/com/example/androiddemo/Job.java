package com.example.androiddemo;

public class Job {
    private String name;
    private int id, listId;

    public String getName() {
        if(name != null)
            return name;
        else
            return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    @Override
    public String toString() {
        return "Job " +
                "name= " + name +
                "; id= " + id +
                "; listID= " + listId;
    }
}
