package com.example.newsclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.newsclient.models.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public NewsAdapter(Context context) {
        this.context = context;
        this.newsList = new ArrayList<>();
    }

    public void setNewsList(List<NewsItem> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    public void addNewsList(List<NewsItem> additionalNewsList) {
        newsList.clear();
        newsList.addAll(additionalNewsList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);
        holder.bind(newsItem);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView titleTextView;
        private TextView dateTextView;
        private TextView authorTextView;
        private TextView authorsTextView;
        private ImageView newsImageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            authorTextView = itemView.findViewById(R.id.author_text_view);
            authorsTextView = itemView.findViewById(R.id.authors_text_view);
            newsImageView = itemView.findViewById(R.id.news_image_view);
            itemView.setOnClickListener(this);
        }

        public void bind(NewsItem newsItem) {
            titleTextView.setText(newsItem.getTitle());
            dateTextView.setText(newsItem.getPublishDate());
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
                authorsTextView.setText("");
            }

            // Настройка параметров загрузки
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Кэширование изображений в памяти и на диске
                    .placeholder(R.drawable.placeholder) // Показывать placeholder во время загрузки
                    .error(R.drawable.error); // Показывать изображение в случае ошибки загрузки

            // Загрузка изображения и отображение в ImageView
            Glide.with(context)
                    .load(newsItem.getImage())
                    .apply(requestOptions) // Применение параметров загрузки
                    .into(newsImageView);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                onItemClickListener.onItemClick(newsList.get(position));
            }
        }
    }
}
