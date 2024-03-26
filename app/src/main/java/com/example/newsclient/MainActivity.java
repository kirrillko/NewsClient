package com.example.newsclient;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsclient.models.NewsItem;
import com.example.newsclient.models.NewsResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String[] TOPICS = {"Политика", "Экономика", "Спорт", "Культура", "Технологии", "Наука", "Здоровье", "Путешествия", "Образование", "Развлечения"};
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private Spinner spinnerTopics;
    List<NewsItem> newsList;
    TextView filterText;
    int filter = 0;
    Date  filterDataDate;
    String filterDataText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        filterText = findViewById(R.id.filter_text_view);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this);
        recyclerView.setAdapter(newsAdapter);

        // Установка слушателя кликов
        newsAdapter.setOnItemClickListener(newsItem -> {
            // Создаем интент для перехода на NewsDetailActivity
            Intent intent = new Intent(this, NewsDetailActivity.class);
            // Устанавливаем объект NewsItem в интент
            intent.putExtra("news_item", newsItem);
            // Запускаем NewsDetailActivity
            startActivity(intent);
        });

        spinnerTopics = findViewById(R.id.spinner_topics);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TOPICS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTopics.setAdapter(adapter);

        spinnerTopics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedTopic = spinnerTopics.getSelectedItem().toString();
                Toast.makeText(MainActivity.this, "Выбранная тема: " + selectedTopic, LENGTH_SHORT).show();
                // Вызов AsyncTask для выполнения запроса новостей
                new GetNewsTask().execute(selectedTopic);
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // Инициализируем меню
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Новости");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
    }

    private class GetNewsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String topic = params[0];
            // Здесь вызывается функция searchNews с выбранной темой
            // Возвращаем результат запроса
            try {
                return NewsApiClient.searchNews(topic, "ru", 20);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            NewsResponse newsResponse = gson.fromJson(result, NewsResponse.class);

            switch (filter){
                case 0:
                    // Получаем список новостей
                    newsList = newsResponse.getNews();
                    newsAdapter.addNewsList(newsList);
                    break;
                case 1:
                {
                    newsList = newsResponse.getNews();
                    List<NewsItem> sortedList = new ArrayList<>();
                    for (NewsItem item: newsList){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = dateFormat.parse(item.getPublishDate());
                            filterDataDate = date;
                            if(compareDates(date, filterDataDate))
                                sortedList.add(item);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    newsAdapter.addNewsList(sortedList);
                }
                    break;
                case 2:
                {
                    newsList = newsResponse.getNews();
                    List<NewsItem> sortedList = new ArrayList<>();
                    for (NewsItem item: newsList){
                        if(item.getText().toLowerCase().contains(filterDataText.toLowerCase()))
                            sortedList.add(item);
                    }
                    newsAdapter.addNewsList(sortedList);
                }
                    break;
                case 3:
                {
                    newsList = newsResponse.getNews();
                    List<NewsItem> sortedList = new ArrayList<>(newsList);
                    sortByDateAscending(sortedList);
                    newsAdapter.addNewsList(sortedList);
                }
                    break;
                case 4:
                {
                    newsList = newsResponse.getNews();
                    List<NewsItem> sortedList = new ArrayList<>(newsList);
                    sortByDateDescending(sortedList);
                    newsAdapter.addNewsList(sortedList);
                    filter = 4;
                }
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item_filter_date){
            showDatePickerDialog();
            filter = 1;
            filterText.setText("По дате");
        }else if(id == R.id.item_filter_text){
            showInputDialog();
            filter = 2;
            filterText.setText("По тексту");
        }else if(id == R.id.item_sort_up){
            List<NewsItem> sortedList = new ArrayList<>(newsList);
            sortByDateAscending(sortedList);
            newsAdapter.addNewsList(sortedList);
            filter = 3;
            filterText.setText("По возрастанию даты");
        }else if(id == R.id.item_sort_down){
            List<NewsItem> sortedList = new ArrayList<>(newsList);
            sortByDateDescending(sortedList);
            newsAdapter.addNewsList(sortedList);
            filter = 4;
            filterText.setText("По убыванию даты");
        }else if(id == R.id.item_reset){
            newsAdapter.addNewsList(newsList);
            filterText.setText("");
            filter = 0;
        }
        return false;
    }

    private void showDatePickerDialog() {
        // Получить текущую дату
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Создать диалог выбора даты
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth1) -> {
            // Создаем объект Calendar и устанавливаем в него переданные значения года, месяца и дня
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.YEAR, year1);
            calendar1.set(Calendar.MONTH, month1);
            calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth1);

            // Получаем объект даты из объекта Calendar
            Date selectedDate = calendar1.getTime();

            List<NewsItem> sortedList = new ArrayList<>();
            for (NewsItem item: newsList){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = dateFormat.parse(item.getPublishDate());
                    filterDataDate = date;
                    if(compareDates(date, selectedDate))
                        sortedList.add(item);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            newsAdapter.addNewsList(sortedList);
        }, year, month, dayOfMonth);

        // Показать диалог выбора даты
        datePickerDialog.show();
    }

    private void showInputDialog() {
        // Создание диалогового окна
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите строку");

        // Настройка макета для диалогового окна
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input, null);
        builder.setView(dialogView);

        // Получение ссылки на EditText
        final EditText input = dialogView.findViewById(R.id.input);

        // Настройка кнопок "ОК" и "Отмена"
        builder.setPositiveButton("OK", (dialog, which) -> {
            String userInput = input.getText().toString();
            filterDataText = userInput;

            List<NewsItem> sortedList = new ArrayList<>();
            for (NewsItem item: newsList){
                if(item.getText().toLowerCase().contains(userInput.toLowerCase()))
                    sortedList.add(item);
            }
            newsAdapter.addNewsList(sortedList);
        });
        builder.setNegativeButton("Отмена", null);

        // Отображение диалогового окна
        builder.show();
    }

    // Метод для сортировки списка по возрастанию даты
    private static void sortByDateAscending(List<NewsItem> objects) {
        Collections.sort(objects, (o1, o2) -> {
            Date date1 = parseDate(o1.getPublishDate());
            Date date2 = parseDate(o2.getPublishDate());
            return date1.compareTo(date2);
        });
    }

    // Метод для сортировки списка по убыванию даты
    private static void sortByDateDescending(List<NewsItem> objects) {
        Collections.sort(objects, (o1, o2) -> {
            Date date1 = parseDate(o1.getPublishDate());
            Date date2 = parseDate(o2.getPublishDate());
            return date2.compareTo(date1);
        });
    }

    // Метод для преобразования строки с датой в объект Date
    private static Date parseDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  boolean compareDates(Date date1, Date date2) {
        // Преобразуем объекты Date в объекты Calendar
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        // Сравниваем год, месяц и день для обоих объектов
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}