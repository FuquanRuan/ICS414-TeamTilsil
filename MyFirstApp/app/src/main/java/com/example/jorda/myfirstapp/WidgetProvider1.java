package com.example.jorda.myfirstapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by jorda on 11/15/2015.
 */

public class WidgetProvider1 extends AppWidgetProvider{
    public static String YOUR_AWESOME_ACTION = "YourAwesomeAction";
    public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
    public static boolean pillAppear1 = false;
    public static boolean pillAppear2 = false;
    public static boolean pillAppear3 = false;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for(int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent intent = new Intent(context, WidgetProvider1.class);
            intent.setAction(ACTION_WIDGET_REFRESH);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            remoteViews.setImageViewResource(R.id.imageView, 0);
            remoteViews.setImageViewResource(R.id.imageView2, 0);
            remoteViews.setImageViewResource(R.id.imageView3, 0);
            remoteViews.setOnClickPendingIntent(R.id.imageView, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.imageView2, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.imageView3, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetProvider1.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

        if(intent.getAction().equals(ACTION_WIDGET_REFRESH) && pillAppear1 == true)
        {
            for(int i = 0; i < appWidgetIds.length; i++) {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                if(pillAppear3 == true)
                {
                    remoteViews.setImageViewResource(R.id.imageView3, 0);
                    pillAppear3 = false;
                }else if(pillAppear2 == true)
                {
                    remoteViews.setImageViewResource(R.id.imageView2, 0);
                    pillAppear2 = false;
                }else if(pillAppear1 == true)
                {
                    remoteViews.setImageViewResource(R.id.imageView, 0);
                    pillAppear1 = false;
                }
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
            }
        }

        if(intent.hasExtra("TIMEISUP"))
        {
            for(int i = 0; i < appWidgetIds.length; i++) {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

                if(pillAppear1 == false)
                {
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.pill);
                    pillAppear1 = true;
                }else if(pillAppear2 == false)
                {
                    remoteViews.setImageViewResource(R.id.imageView2, R.drawable.pill);
                    pillAppear2 = true;
                }else if(pillAppear3 == false)
                {
                    remoteViews.setImageViewResource(R.id.imageView3, R.drawable.pill);
                    pillAppear3 = true;
                }
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
            }
        }


    }


}
