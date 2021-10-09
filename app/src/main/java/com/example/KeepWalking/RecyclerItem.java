package com.example.keepwalking;

public class RecyclerItem {
    String date;
    String time;
    String result;

    String getDate() {
        return this.date;
    }

    String getTime() {
        return this.time;
    }

    String getResult() { return this.result;}

    RecyclerItem(String date, String time, String result) {
        this.date = date;
        this.time = time;
        this.result = result;
    }
}
