package com.example.newsclient;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.newsclient.models.NewsItem;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Получаем данные новости из интента (или любого другого места)
        NewsItem newsItem = getIntent().getParcelableExtra("news_item");

        // Находим текстовые поля в макете
        TextView titleTextView = findViewById(R.id.title_text_view);
        TextView textTextView = findViewById(R.id.text_text_view);
        TextView urlTextView = findViewById(R.id.url_text_view);
        TextView publishDateTextView = findViewById(R.id.publish_date_text_view);
        TextView authorTextView = findViewById(R.id.author_text_view);
        TextView authorsTextView = findViewById(R.id.authors_text_view);
        TextView languageTextView = findViewById(R.id.language_text_view);
        TextView sourceCountryTextView = findViewById(R.id.source_country_text_view);
        ImageView newsImageView = findViewById(R.id.photo_image_view);

        // Настройка параметров загрузки
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Кэширование изображений в памяти и на диске
                .placeholder(R.drawable.placeholder) // Показывать placeholder во время загрузки
                .error(R.drawable.error); // Показывать изображение в случае ошибки загрузки

        // Загрузка изображения и отображение в ImageView
        Glide.with(this)
                .load(newsItem.getImage())
                .apply(requestOptions) // Применение параметров загрузки
                .into(newsImageView);

        // Заполняем текстовые поля данными новости
        assert newsItem != null;
        titleTextView.setText(newsItem.getTitle());
        textTextView.setText(newsItem.getText());
        urlTextView.setText(newsItem.getUrl());
        publishDateTextView.setText(newsItem.getPublishDate());
        authorTextView.setText(newsItem.getAuthor());

        if(newsItem.getAuthors().size() > 1)
        {
            StringBuilder allAuthors = new StringBuilder();
            for (String author : newsItem.getAuthors()) {
                allAuthors.append(author).append(", ");
            }
            if (allAuthors.length() > 0) {
                allAuthors.delete(allAuthors.length() - 2, allAuthors.length());
            }
            authorsTextView.setText(allAuthors.toString());
        }else{
            authorsTextView.setVisibility(View.GONE);
        }
        languageTextView.setVisibility(View.GONE);
        sourceCountryTextView.setText("Страна: " + newsItem.getSourceCountry());
    }
}