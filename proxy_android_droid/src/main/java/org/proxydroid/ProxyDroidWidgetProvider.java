/*
package org.proxydroid;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import org.proxydroid.utils.Utils;

public class ProxyDroidWidgetProvider extends AppWidgetProvider {

	public static final String PROXY_SWITCH_ACTION = "org.proxydroid.ProxyDroidWidgetProvider.PROXY_SWITCH_ACTION";
	public static final String SERVICE_NAME = "org.proxydroid.ProxyDroidService";
	public static final String TAG = "ProxyDroidWidgetProvider";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			// Create an Intent to launch ExampleActivity
			Intent intent = new Intent(context, ProxyDroidWidgetProvider.class);
			intent.setAction(PROXY_SWITCH_ACTION);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					0, intent, 0);

			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.proxydroid_appwidget);
			views.setOnClickPendingIntent(R.id.serviceToggle, pendingIntent);

			if (Utils.isWorking()) {
				views.setImageViewResource(R.id.serviceToggle, R.drawable.on);
				Log.d(TAG, "Service running");
			} else {
				views.setImageViewResource(R.id.serviceToggle, R.drawable.off);
				Log.d(TAG, "Service stopped");
			}

			// Tell the AppWidgetManager to perform an update on the current App
			// Widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if (intent.getAction().equals(PROXY_SWITCH_ACTION)) {
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.proxydroid_appwidget);
			try {
				views.setImageViewResource(R.id.serviceToggle, R.drawable.ing);

				AppWidgetManager awm = AppWidgetManager.getInstance(context);
				awm.updateAppWidget(awm.getAppWidgetIds(new ComponentName(
						context, ProxyDroidWidgetProvider.class)), views);
			} catch (Exception ignore) {
				// Nothing
			}

			Log.d(TAG, "Proxy switch action");
			// do some really cool stuff here
			if (Utils.isWorking()) {
				// Service is working, so stop it
				try {
					context.stopService(new Intent(context,
							ProxyDroidService.class));
				} catch (Exception e) {
					// Nothing
				}

			} else {

				// Service is not working, then start it
				SharedPreferences settings = PreferenceManager
						.getDefaultSharedPreferences(context);

				Profile mProfile = new Profile();
				mProfile.getProfile(settings);

				Intent it = new Intent(context, ProxyDroidService.class);
				Bundle bundle = new Bundle();
				bundle.putString("host", mProfile.getHost());
				bundle.putString("user", mProfile.getUser());
				bundle.putString("bypassAddrs", mProfile.getBypassAddrs());
				bundle.putString("password", mProfile.getPassword());
				bundle.putString("domain", mProfile.getDomain());

				bundle.putString("proxyType", mProfile.getProxyType());
				bundle.putBoolean("isAutoSetProxy", mProfile.isAutoSetProxy());
				bundle.putBoolean("isBypassApps", mProfile.isBypassApps());
				bundle.putBoolean("isAuth", mProfile.isAuth());
				bundle.putBoolean("isNTLM", mProfile.isNTLM());
				bundle.putBoolean("isDNSProxy", mProfile.isDNSProxy());
				bundle.putBoolean("isPAC", mProfile.isPAC());

				bundle.putInt("port", mProfile.getPort());

				it.putExtras(bundle);
				context.startService(it);

			}

		}
	}
}
*/
