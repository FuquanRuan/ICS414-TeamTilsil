package com.example.jorda.myfirstapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MediTAKE";
    private String urlPath = "http://courses.ics.hawaii.edu/ics321f15/schedule";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MyTask newTask = new MyTask();
        newTask.execute(urlPath);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MyTask reloadTask = new MyTask();
                reloadTask.execute("http://courses.ics.hawaii.edu/ics321f15/schedule");
                loadFile();
            }
        }, 0, 60000);

    }

    public void onAddEvent(View v)

    {
        setContentView(R.layout.addevent_layout);
    }

    public void onBackHome(View v)

    {
        setContentView(R.layout.activity_main);
        loadFile();
    }

    public void saveNewEvent(View v)
    {
        DatePicker datepicker = (DatePicker)findViewById(R.id.datePicker);
        TimePicker timepicker = (TimePicker)findViewById(R.id.timePicker);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Calendar c = Calendar.getInstance();
        String inputDateString = datepicker.getYear() + "/" + (datepicker.getMonth() + 1) + "/" + datepicker.getDayOfMonth();
        String inputTimeString = timepicker.getCurrentHour() + ":" + timepicker.getCurrentMinute();
        try {
            Date today = dateFormat.parse(dateFormat.format(c.getTime()));
            Date inputDate = dateFormat.parse(inputDateString + " " + inputTimeString);
            if(inputDate.compareTo(today) >= 1)
            {
                try {
                    FileWriter fw = new FileWriter(path + "saveFile.txt", true);
                    BufferedWriter bf = new BufferedWriter(fw);
                    bf.write(inputDateString);
                    bf.write("|" + inputTimeString + "\n");
                    bf.close();
                    setContentView(R.layout.activity_main);
                    loadFile();

                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }else
            {
                Toast myToast = Toast.makeText(getApplicationContext(), "Incorrect Date and Time", Toast.LENGTH_LONG);
                myToast.show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    public void loadFile()
    {
        String line = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Calendar c = Calendar.getInstance();
        ListView lv = (ListView)findViewById(R.id.listView);
        List<String> listArray = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path + "saveFile.txt"));
            BufferedReader URLbr = new BufferedReader(new FileReader(path + "_URL_File.txt"));
            while((line = br.readLine()) != null)
            {
                String[] lineArray = line.split("\\|");
                Date date = dateFormat.parse(lineArray[0] + " " + lineArray[1]);
                Date today = dateFormat.parse(dateFormat.format(c.getTime()));
                if(date.compareTo(today) >= 1)
                {
                    listArray.add(lineArray[0] + "\n" +lineArray[1]);
                }
            }

            while((line = URLbr.readLine()) != null)
            {
                String[] lineArray = line.split("\\|");
                Date date = dateFormat.parse(lineArray[0] + " " + lineArray[1]);
                Date today = dateFormat.parse(dateFormat.format(c.getTime()));
                if(date.compareTo(today) >= 1)
                {
                    listArray.add(lineArray[0] + "\n" + lineArray[1]);
                }

            }
            ListAdapter adapterLine = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listArray);
            lv.setAdapter(adapterLine);

            URLbr.close();
            br.close();

        } catch (java.io.IOException e) {
            File fl = new File(path + "saveFile.txt");
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

//class MyTimeTask extends TimerTask
//{
//
//    @Override
//    public void run() {
//        MyTask reloadTask = new MyTask();
//        reloadTask.execute("http://courses.ics.hawaii.edu/ics321f15/schedule");
//        MainActivity mainActivity = new MainActivity();
//        mainActivity.loadFile();
//    }
//}

class MyTask extends AsyncTask<String, Void, Boolean>
{
    @Override
    protected Boolean doInBackground(String... params) {
        boolean successful = false;
        URL url = null;
        HttpURLConnection connection = null;
        String line = "";
        int count = 0;
        try {
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MediTAKE_URL_File.txt"));
                connection.setDoInput(true);
                connection.setDoOutput(true);
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while((line = br.readLine()) != null)
                {
                    line = line.replaceAll("\\s{2,}", "");
                    if(line.matches("[+-]?\\d*\\.?\\d+"))
                    {
                        br.readLine();
                        br.readLine();
                        String[] date = br.readLine().replaceAll("\\s{2,}", "").split("\\s");
                        for(int i = 0; i <date.length; i++)

                        date[1] = date[1].replace("Jan", "1");
                        date[1] = date[1].replace("Feb", "2");
                        date[1] = date[1].replace("Mar", "3");
                        date[1] = date[1].replace("Apr", "4");
                        date[1] = date[1].replace("May", "5");
                        date[1] = date[1].replace("Jun", "6");
                        date[1] = date[1].replace("Jul", "7");
                        date[1] = date[1].replace("Aug", "8");
                        date[1] = date[1].replace("Sep", "9");
                        date[1] = date[1].replace("Oct", "10");
                        date[1] = date[1].replace("Nov", "11");
                        date[1] = date[1].replace("Dec", "12");
                        bw.write("2015/" + date[1] + "/" + date[2] + "|" + "8:00|\n");

                        count++;
                    }
                }
                bw.close();
                successful = true;
            }
            finally{
                connection.disconnect();
            }




        } catch (IOException e) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MediTAKEtest.txt"));
                bw.write("This is a test fail");
                bw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

        Log.d("MyApp", count + "");
        return successful;
    }
}
