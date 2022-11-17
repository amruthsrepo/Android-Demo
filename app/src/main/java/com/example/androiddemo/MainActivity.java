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

//    List of all the jobs after filtering
    ArrayList<Job> filteredJobs;

//    View objects to handle changes in view
    ProgressBar loadingProgressBar;
    TextView loadingTextView;
    TextView failTextView;
    ArrayAdapter<Job> adapter;
    ListView jobsListView;

//    HTTP client to perform HTTP requests
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        Set basic view components
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_title));

//        Initializing array list
        filteredJobs = new ArrayList<>();

//        Initializing view components
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        loadingTextView = findViewById(R.id.loadingTextView);
        failTextView = findViewById(R.id.failTextView);
        jobsListView = findViewById(R.id.jobsListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, filteredJobs);

//        Set adapter for list view
        jobsListView.setAdapter(adapter);

//        Call function to get and display the list of jobs
        getJobs();

    }


    /*
    * Function to call API to get the list of jobs
    * The list is then filtered and sorted
    * Finally it is displayed using the ListView component
    */
    public void getJobs() {

//        Get url value from resources
        String jobsURL = getString(R.string.jobs_url);

//        Initializing request object
        Request request = new Request.Builder()
                .url(jobsURL)
                .build();

//        API call function begins
        client.newCall(request).enqueue(new Callback() {

//            Handling code for request failed
            @Override public void onFailure(Call call, IOException e) {
                loadingProgressBar.setVisibility(View.GONE);
                loadingTextView.setVisibility(View.GONE);
                failTextView.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
//                    Handling code for unexpected response
                    if (!response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingProgressBar.setVisibility(View.GONE);
                                loadingTextView.setVisibility(View.GONE);
                                failTextView.setVisibility(View.VISIBLE);
                                new IOException("Unexpected code " + response).printStackTrace();
                            }
                        });
                    }

//                    Using GSON to parse the response json
                    final GsonBuilder gsonBuilder = new GsonBuilder();
                    final Gson gson = gsonBuilder.create();
                    Job[] jobs = gson.fromJson(responseBody.charStream(), Job[].class);

//                    Filtering jobs with invalid name
                    for (Job j : jobs) {
                        if(j.getName().trim().length() > 0 && !j.getName().trim().equalsIgnoreCase("null")) {
                            filteredJobs.add(j);
                        }
                    }

//                    Sorting remaining jobs
                    filteredJobs.sort(new Comparator<Job>() {
                        @Override
                        public int compare(Job j1, Job j2) {
                            if(j1.getListId() - j2.getListId() == 0) {
//                                Lexicographically comparing the names of jobs
                                return j1.getName().compareTo(j2.getName()) > 0 ? 1 : -1;
                            } else {
//                                Comparing the int value ListId of jobs
                                return j1.getListId() > j2.getListId()  ? 1 : -1;
                            }
                        }
                    });

//                    Updating view components by running on the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Update adapter
                            adapter.notifyDataSetChanged();

//                            Hide loading components
                            loadingProgressBar.setVisibility(View.GONE);
                            loadingTextView.setVisibility(View.GONE);

//                            Un-hide list component
                            jobsListView.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (Exception e){
                    e.printStackTrace();
//                    Handling code for request failed
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingProgressBar.setVisibility(View.GONE);
                            loadingTextView.setVisibility(View.GONE);
                            failTextView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }
}