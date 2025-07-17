// ApiClient.java
package com.example.workforcemanagement.data.api;

import com.example.workforcemanagement.data.model.Employee;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.util.BooleanTypeAdapter;
import com.example.workforcemanagement.util.Constants;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final int TIMEOUT = 60;
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .build();

            // Tạo Gson với BooleanTypeAdapter
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
            gsonBuilder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private static class ProfileDeserializer implements JsonDeserializer<Object> {
        @Override
        public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonElement profileTypeElement = json.getAsJsonObject().get("profile_type");
            String profileType = profileTypeElement != null ? profileTypeElement.getAsString() : null;

            if ("admin".equals(profileType)) {
                return context.deserialize(json, User.class);
            } else {
                return context.deserialize(json, Employee.class);
            }
        }
    }
}
