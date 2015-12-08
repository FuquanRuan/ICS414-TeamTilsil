package com.example.jorda.myfirstapp;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import static android.app.Notification.DEFAULT_LIGHTS;


public class MainActivity extends AppCompatActivity {

    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MediTAKE";
    private String urlPath = "http://courses.ics.hawaii.edu/ics321f15/schedule";
    public static List<Date> dateList;
    public static int countSchedule = 0;
    public Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.loadFile();
    }

    private void setSchedule()
    {
        while(countSchedule > 0)
        {
            timer.cancel();
            timer.purge();
            countSchedule--;
        }
        timer = new Timer();
        int yearTakeOut = 1900;
        while(!dateList.isEmpty()) {
            countSchedule++;
            Date newDate = dateList.remove(0);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intentSendTextToWidgte = new Intent(MainActivity.this, WidgetProvider1.class);
                    intentSendTextToWidgte.setAction(AppWidgetManager.EXTRA_CUSTOM_EXTRAS);
                    intentSendTextToWidgte.putExtra("TIMEISUP", "TIMEISUP");
                    sendBroadcast(intentSendTextToWidgte);

                    NotificationCompat.Builder note = new NotificationCompat.Builder(MainActivity.this)
                            .setContentTitle("MediTake")
                            .setContentText("Take Your Medicine!")
                            .setSmallIcon(R.drawable.pill);

                    note.setDefaults(Notification.DEFAULT_ALL);
                    note.setAutoCancel(true);

                    NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    mgr.notify(1, note.build());
                }
            }, newDate);
        }
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
        dateList = new ArrayList<Date>();
        String line = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Calendar c = Calendar.getInstance();
        ListView lv = (ListView)findViewById(R.id.listView);
        List<String> listArray = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path + "saveFile.txt"));
            while((line = br.readLine()) != null)
            {
                String[] lineArray = line.split("\\|");
                Date date = dateFormat.parse(lineArray[0] + " " + lineArray[1]);
                Date today = dateFormat.parse(dateFormat.format(c.getTime()));
                if(date.compareTo(today) >= 1)
                {
                    listArray.add(lineArray[0] + "\n" +lineArray[1]);
                    dateList.add(date);
                }
            }
            br.close();

            BufferedReader URLbr = new BufferedReader(new FileReader(path + "_URL_File.txt"));

            while((line = URLbr.readLine()) != null)
            {
                String[] lineArray = line.split("\\|");
                Date date = dateFormat.parse(lineArray[0] + " " + lineArray[1]);
                Date today = dateFormat.parse(dateFormat.format(c.getTime()));
                if(date.compareTo(today) >= 1)
                {
                    listArray.add(lineArray[0] + "\n" + lineArray[1]);
                    dateList.add(date);
                }

            }
//            ListAdapter adapterLine = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listArray);
            URLbr.close();

        } catch (java.io.IOException e) {
            File fl = new File(path + "saveFile.txt");
            if(!fl.exists())
            {
                try {
                    fl.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        lv.setAdapter(new MyListAdaper(this, R.layout.list_item, listArray));
        this.setSchedule();
    }

    public void loadURLdata(View v)
    {
        MyTask newTask = new MyTask();
        newTask.execute(urlPath);
        while(newTask.isFinish() == false)
        {
            Log.i("Background", "Background still running");
        }
        int count = newTask.glCount;
        loadFile();
        Toast myToast = Toast.makeText(getApplicationContext(), "Loaded " + count + " reminders!", Toast.LENGTH_LONG);
        myToast.show();
        newTask.setIsFinish(false);
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

    public boolean removeDate(String selectedStr)
    {
        boolean isRemove = false;
        List<String> localData = new ArrayList<String>();
        List<String> urlData = new ArrayList<String>();

        String line = "";
        try {
            isRemove = true;
            BufferedReader br = new BufferedReader(new FileReader(path + "saveFile.txt"));
            while((line = br.readLine()) != null)
            {
                if(!line.contains(selectedStr))
                {
                    localData.add(line);
                }
            }

            br.close();

            FileWriter fw = new FileWriter(path + "saveFile.txt");
            BufferedWriter bf = new BufferedWriter(fw);
            while(!localData.isEmpty())
            {
                String temp = localData.remove(0);
                bf.write(temp + "\n");
                Log.i("removelocal", temp);
            }
            bf.close();

            BufferedReader URLbr = new BufferedReader(new FileReader(path + "_URL_File.txt"));
            while((line = URLbr.readLine()) != null)
            {
                if(!line.contains(selectedStr))
                {
                    urlData.add(line);
                }
            }
            URLbr.close();

            FileWriter urlFw = new FileWriter(path + "_URL_File.txt");
            BufferedWriter urlBf = new BufferedWriter(urlFw);
            while(!urlData.isEmpty())
            {
                String temp = urlData.remove(0);
                urlBf.write(temp + "\n");
                Log.i("removeURL", temp);
            }
            urlBf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return isRemove;
    }

    private class MyListAdaper extends ArrayAdapter<String>
    {
        private int layout;
        public MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public  View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder mainViewHolder = null;
            if(convertView == null)
            {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_removeBtn);
                viewHolder.button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String selectedStr = (String) viewHolder.title.getText();
                        selectedStr = selectedStr.replace("\n", "|");
                        Log.i("selected", selectedStr);
                        removeDate(selectedStr);
                        loadFile();
                    }
                });
                convertView.setTag(viewHolder);

            }
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.title.setText(getItem(position).toString());

            return convertView;
        }
    }

    public class ViewHolder
    {
        TextView title;
        Button button;
    }
}

class MyTask extends AsyncTask<String, Void, Boolean>
{
    public static int glCount;
    public static boolean isFinish;
    @Override
    protected Boolean doInBackground(String... params) {
        isFinish = false;
        glCount = 0;
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
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        Date today = dateFormat.parse(dateFormat.format(c.getTime()));
                        Date inputDate = dateFormat.parse("2015/" + date[1] + "/" + date[2] + " " + "8:00");
                        if(inputDate.compareTo(today) >= 1)
                        {
                            bw.write("2015/" + date[1] + "/" + date[2] + "|" + "8:00|\n");
                            count++;
                        }

                    }
                }
                bw.close();
                successful = true;
            } catch (ParseException e) {
                e.printStackTrace();
            } finally{
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
        glCount = count;
        isFinish = true;
        return successful;
    }

    public int returnCount()
    {
        return glCount;
    }

    public boolean isFinish()
    {
     return isFinish;
    }

    public void setIsFinish(boolean boolIsFinish)
    {
        this.isFinish = boolIsFinish;
    }
}
