package com.example.newsclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResponse {
    private int offset;
    private int number;
    private int available;
    @SerializedName("news")
    private List<NewsItem> news;

    // Геттеры и сеттеры для всех полей

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public List<NewsItem> getNews() {
        return news;
    }

    public void setNews(List<NewsItem> news) {
        this.news = news;
    }
}
