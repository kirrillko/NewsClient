package com.example.newsclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NewsApiClient {

    private static final String API_KEY = "722a816e91464afca764bf47c192ede5";

    // Функция для выполнения запроса к API и получения ответа
    public static String searchNews(String searchText, String language, int number) throws IOException {
        // Заменяем пробелы и другие специальные символы в строке поиска
        String encodedSearchText = URLEncoder.encode(searchText, "UTF-8");
        // Формируем URL для запроса с учетом переданных параметров
        String apiUrl = "https://api.worldnewsapi.com/search-news?text=" + encodedSearchText +
                "&language=" + language + "&number=" + number;

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Устанавливаем заголовок с ключом API
        connection.setRequestProperty("X-Api-Key", API_KEY);

        // Считываем ответ от сервера
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        connection.disconnect();

        return response.toString();
    }
}
