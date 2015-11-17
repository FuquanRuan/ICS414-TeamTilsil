package com.example.jorda.myfirstapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

/**
 * Created by jorda on 11/15/2015.
 */

public class WidgetProvider1 extends AppWidgetProvider{
    public static String YOUR_AWESOME_ACTION = "YourAwesomeAction";
    public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
    public static boolean pillAppear = false;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for(int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent intent = new Intent(context, WidgetProvider1.class);
            intent.setAction(ACTION_WIDGET_REFRESH);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.imageView, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetProvider1.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

//        if(imageView.getVisibility() == View.VISIBLE) {
//            for (int i = 0; i < appWidgetIds.length; i++) {
//                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//                imageView.setVisibility(View.INVISIBLE);
//                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
//            }
//        }

        if(intent.getAction().equals(ACTION_WIDGET_REFRESH) && pillAppear == false)
        {
            for(int i = 0; i < appWidgetIds.length; i++) {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                remoteViews.setImageViewResource(R.id.imageView, R.drawable.pill);
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
                pillAppear = true;
            }
        }else if(pillAppear == true)
        {
            for(int i = 0; i < appWidgetIds.length; i++) {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                remoteViews.setImageViewResource(R.id.imageView, 0);
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
                pillAppear = false;
            }
        }



    }


}
