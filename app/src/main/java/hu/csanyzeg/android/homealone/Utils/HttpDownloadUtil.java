package hu.csanyzeg.android.homealone.Utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tanulo on 2018. 08. 13..
 */

public abstract class HttpDownloadUtil {
    protected HttpDownloadUtil() {
    }

    public abstract void onDownloadStart();
    public abstract void onDownloadComplete(StringBuilder stringBuilder);
    private boolean inProgress = false;
    private Date begin;
    private Date end;

    public long getDownloadTimeMs(){
        return end.getTime() - begin.getTime();
    }

    public static class HttpRequestInfo{
        public String url;
        public Method method;
        public HashMap<String, String> getValues;
        public HashMap<String, String> postValues;
        public int connectionTimeout = 2000;
        public int readTimeout = 2000;

        public HttpRequestInfo(String url, Method method, HashMap<String, String> getValues, HashMap<String, String> postValues) {
            this.url = url;
            this.method = method;
            this.getValues = getValues;
            this.postValues = postValues;
        }
    }

    private class HttpAsyncTask extends AsyncTask<HttpRequestInfo, Integer, StringBuilder> {
        @Override
        protected void onPreExecute() {
            inProgress = true;
            super.onPreExecute();
            onDownloadStart();
            begin = Calendar.getInstance().getTime();
        }

        @Override
        protected void onPostExecute(StringBuilder s) {
            super.onPostExecute(s);

            end = Calendar.getInstance().getTime();
            inProgress = false;
            onDownloadComplete(s);
        }

        @Override
        protected StringBuilder doInBackground(HttpRequestInfo... httpRequestInfos) {
            HttpRequestInfo info = httpRequestInfos[0];
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder stringBuilder = new StringBuilder();
            boolean err = false;
            try {

                url = new URL(info.url + "/?" + HttpMapUtil.mapToString(info.getValues));
                System.out.println("GET: " +url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(info.connectionTimeout);
                urlConnection.setReadTimeout(info.readTimeout);

                urlConnection.setDoInput(true);
                switch (info.method){
                    case GET:
                        urlConnection.setRequestMethod("GET");
                        break;
                    case POST:
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setDoOutput(true);
                        break;
                }

                if (info.method == Method.POST) {
                    //System.out.printf(" POST values start");
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(HttpMapUtil.mapToString(info.postValues));
                    writer.flush();
                    //System.out.printf(" POST values end");
                }

                //System.out.println(" Waiting");
                long start = System.currentTimeMillis();

                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                long end = System.currentTimeMillis();
                //System.out.println(" Response " + (end - start) + " ms");

                int data;
                data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    data = reader.read();
                    stringBuilder.append(current);
                }
                //System.out.println(" End HTTP connection");
            } catch (Exception e) {
                err = true;
                if (e.getMessage() == null) {
                    Log.e("HTTP", Log.getStackTraceString(e));
                } else {
                    Log.e("HTTP", e.getMessage());
                }
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            if (err){
                return null;
            }
            return stringBuilder;
        }
    }


    public enum Method{
        POST,
        GET
    }

    public void download(HttpRequestInfo info){
        new HttpAsyncTask().execute(info);
    }

    public boolean isInProgress() {
        return inProgress;
    }
}
