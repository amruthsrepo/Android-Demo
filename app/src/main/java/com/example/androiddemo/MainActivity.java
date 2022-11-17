package com.example.androiddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    ArrayList<Job> filteredJobs;

    ProgressBar loadingProgressBar;
    TextView loadingTextView;
    TextView failTextView;
    ArrayAdapter<Job> adapter;
    ListView jobsListView;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_title));

        filteredJobs = new ArrayList<>();

        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        loadingTextView = findViewById(R.id.loadingTextView);
        failTextView = findViewById(R.id.failTextView);
        jobsListView = findViewById(R.id.jobsListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, filteredJobs);

        jobsListView.setAdapter(adapter);

        getJobs();

    }

    /**/
    public void getJobs() {
        String jobsURL = getString(R.string.jobs_url);

        Request request = new Request.Builder()
                .url(jobsURL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                loadingProgressBar.setVisibility(View.GONE);
                loadingTextView.setVisibility(View.GONE);
                failTextView.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        loadingProgressBar.setVisibility(View.GONE);
                        loadingTextView.setVisibility(View.GONE);
                        failTextView.setVisibility(View.VISIBLE);
                        new IOException("Unexpected code " + response).printStackTrace();
                    }

                    final GsonBuilder gsonBuilder = new GsonBuilder();
                    final Gson gson = gsonBuilder.create();
                    Job[] jobs = gson.fromJson(responseBody.charStream(), Job[].class);

                    for (Job j : jobs) {
                        if(j.getName().trim().length() > 0 && !j.getName().trim().equalsIgnoreCase("null")) {
                            filteredJobs.add(j);
                        }
                    }

                    filteredJobs.sort(new Comparator<Job>() {
                        @Override
                        public int compare(Job j1, Job j2) {
                            if(j1.getListId() - j2.getListId() == 0) {
                                return j1.getName().compareTo(j2.getName()) > 0 ? 1 : -1;
                            } else {
                                return j1.getListId() > j2.getListId()  ? 1 : -1;
                            }
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            loadingProgressBar.setVisibility(View.GONE);
                            loadingTextView.setVisibility(View.GONE);
                            jobsListView.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (Exception e){
                    loadingProgressBar.setVisibility(View.GONE);
                    loadingTextView.setVisibility(View.GONE);
                    failTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}