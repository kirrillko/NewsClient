package com.example.newsclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NewsItem  implements Parcelable {
    private long id;
    private String title;
    private String text;
    private String url;
    private String image;
    @SerializedName("publish_date")
    private String publishDate;
    private String author;
    private List<String> authors;
    private String language;
    @SerializedName("source_country")
    private String sourceCountry;

    // Геттеры и сеттеры для всех полей

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    protected NewsItem(Parcel in) {
        id = in.readLong();
        title = in.readString();
        text = in.readString();
        url = in.readString();
        image = in.readString();
        publishDate = in.readString();
        author = in.readString();
        authors = new ArrayList<>();
        in.readStringList(authors);
        language = in.readString();
        sourceCountry = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeString(url);
        dest.writeString(image);
        dest.writeString(publishDate);
        dest.writeString(author);
        dest.writeStringList(authors);
        dest.writeString(language);
        dest.writeString(sourceCountry);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
}
