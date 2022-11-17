package com.example.androiddemo;

/*
* POJO class to store individual job objects
*/

public class Job {
    private String name;
    private int id, listId;

//    Getter for String name
    public String getName() {
//        Checking the value of name to make sure getName() does not return null
        if(name != null)
            return name;
        else
            return "";
    }

//    Setter for String name
    public void setName(String name) {
        this.name = name;
    }

//    Getter for int id
    public int getId() {
        return id;
    }

//    Setter for int id
    public void setId(int id) {
        this.id = id;
    }

//    Getter for int listId
    public int getListId() {
        return listId;
    }

//    Setter for int listId
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
