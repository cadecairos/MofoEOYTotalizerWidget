package ca.chrisdecairos.cade.mofoeoytotalizerwidget;

        import android.app.PendingIntent;
        import android.appwidget.AppWidgetManager;
        import android.appwidget.AppWidgetProvider;
        import android.content.Context;
        import android.content.Intent;
        import android.widget.RemoteViews;

        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.Request;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.Volley;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.math.RoundingMode;
        import java.text.DecimalFormat;


public class EOYWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://d3gxuc3bq48qfa.cloudfront.net/eoy-2014-total";
        final DecimalFormat df = new DecimalFormat("#,###.##");
        df.setRoundingMode(RoundingMode.FLOOR);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        setText(context, appWidgetManager, appWidgetIds, "$" + df.format(response.getDouble("sum")));
                    } catch (JSONException je) {
                        setText(context, appWidgetManager, appWidgetIds, ":(");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setText(context, appWidgetManager, appWidgetIds, ":(");
                }
            });

        queue.add(jsonRequest);
    }

    private void setText(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String text) {
        final int count = appWidgetIds.length;
        for (int i = 0; i < count; i++) {

            int widgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.eoy_totalizer);
            remoteViews.setTextViewText(R.id.fundsRaised, text);

            Intent intent = new Intent(context, EOYWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.fundsRaised, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
