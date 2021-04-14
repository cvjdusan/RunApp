package rs.ac.bg.etf.running.rest;

import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rs.ac.bg.etf.running.MainActivity;

public class OpenWeatherMapService {

    private static final String API_KEY = "07dc2b1f14132b5b3ffdd3cded996cca";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    private OpenWeatherMapApi openWeatherMapApi;

    public OpenWeatherMapService() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addInterceptor(loggingInterceptor);

        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        openWeatherMapApi = retrofit.create(OpenWeatherMapApi.class);
    }

    public void getCurrentWeather(double latitude, double longitude) {
        Call<CurrentWeatherModel> call = openWeatherMapApi
                .getCurrentWeather(API_KEY, latitude, longitude, "metric");

        call.enqueue(new Callback<CurrentWeatherModel>() {
            @Override
            public void onResponse(
                    @NonNull Call<CurrentWeatherModel> call,
                    @NonNull Response<CurrentWeatherModel> response) {
                if (response.isSuccessful()) {
                    CurrentWeatherModel currentWeatherModel = response.body();
                    Log.d(MainActivity.LOG_TAG, currentWeatherModel.toString());
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<CurrentWeatherModel> call,
                    @NonNull Throwable t) {

            }
        });
    }
}
