/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.weatherlistwidget;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * This is the service that provides the factory to be bound to the collection service.
 */
public class WeatherWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    //private Cursor mCursor;
    static final String[] MAJOR_CITIES = new String[] {
		"San Francisco, CA, USA",
		"Washington, DC, USA",
		"London, England, UK",
		"Addis Ababa, Ethiopia"
	};
    static final int[] TIME_SHIFT_HOURS = new int[] {
    	-8,
    	-5,
    	0,
    	3
    };
    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        // Since we reload the cursor in onDataSetChanged() which gets called immediately after
        // onCreate(), we do nothing here.
    }

    public void onDestroy() {
    	/*
        if (mCursor != null) {
            mCursor.close();
        }
        */
    }

    public int getCount() {
    	return MAJOR_CITIES.length;
        //return mCursor.getCount();
    }
    
    public static int getHourMilliseconds(int hours) {
    	return hours*60*60*1000;
    }

    public RemoteViews getViewAt(int position) {
        // Get the data for this position from the content provider
        String city = "Unknown City";
        int temp = 0;
        /*
        if (mCursor.moveToPosition(position)) {
            final int cityColIndex = mCursor.getColumnIndex(WeatherDataProvider.Columns.CITY);
            final int tempColIndex = mCursor.getColumnIndex(
                    WeatherDataProvider.Columns.TEMPERATURE);
            city = mCursor.getString(cityColIndex);
            temp = mCursor.getInt(tempColIndex);
        }
        */
        city = MAJOR_CITIES[position];
        String time = "";
        long currentTime = System.currentTimeMillis();
        DateFormat df = DateFormat.getInstance();
        
        String[] availableIds = TimeZone.getAvailableIDs(StackRemoteViewsFactory.getHourMilliseconds(TIME_SHIFT_HOURS[position]));
        df.setTimeZone(TimeZone.getTimeZone(availableIds[0]));
        // Return a proper item with the proper city and temperature.  Just for fun, we alternate
        // the items to make the list easier to read.
        final String formatStr = mContext.getResources().getString(R.string.item_format_string);
        final int itemId = (position % 2 == 0 ? R.layout.light_widget_item
                : R.layout.dark_widget_item);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        //rv.setTextViewText(R.id.widget_item, String.format(formatStr, temp, city));
        rv.setTextViewText(R.id.widget_item, String.format(formatStr, temp, city));

        // Set the click intent so that we can handle it and show a toast message
        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();
        extras.putString(WeatherWidgetProvider.EXTRA_CITY_ID, city);
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        return rv;
    }
    public RemoteViews getLoadingView() {
        // We aren't going to return a default loading view in this sample
        return null;
    }

    public int getViewTypeCount() {
        // Technically, we have two types of views (the dark and light background views)
        return 2;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // Refresh the cursor
    	/*
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(WeatherDataProvider.CONTENT_URI, null, null,
                null, null);
                */
    }
}