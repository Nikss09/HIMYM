package com.example.himym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Bitmap myImage[]=new Bitmap[10];
    String name[]=new String[10];
    String image[]=new String[10];
    ListView listView;

    public void openLink(View view){
        String url = "https://www.thetoptens.com/m/taytayxtaytay/301088/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return name.length;
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view=getLayoutInflater().inflate(R.layout.customlistview,null);
            ImageView imageView=(ImageView)view.findViewById(R.id.imageView);
            TextView textView=(TextView)view.findViewById(R.id.textView);

            imageView.setImageBitmap(myImage[i]);
            textView.setText(name[i]);

            return view;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url= new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);

                int data=reader.read();
                while(data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

            return result;
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in=connection.getInputStream();

                Bitmap myBitMap= BitmapFactory.decodeStream(in);
                return myBitMap;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task=new DownloadTask();
        String result=null;

        try{
            result=task.execute("https://www.thetoptens.com/m/taytayxtaytay/301088/").get();
            String[] splitResult=result.split("<a href=\"https://www.thetoptens.com/movies-2020/\">Top 10 Best Movies of 2020</a>");

            int i=0;
            Pattern p= Pattern.compile("<b>(.*?)</b>");
            Matcher m=p.matcher(splitResult[0]);
            while(m.find()){
                name[i]=m.group(1);
                i++;
            }

            image[0] = "https://static.thetoptens.com/img/lists/109588.jpg";

            i = 1;
            p = Pattern.compile("data-fsrc=\"(.*?)\">");
            m = p.matcher(splitResult[0]);
            while (m.find()) {
                image[i] = m.group(1);
                i++;
                Log.i("Blaaaa",""+image[i-1]);
            }


        }catch(Exception e){
            e.printStackTrace();
        }

        ImageDownloader imagetask[]=new ImageDownloader[10];
        for(int i=0;i<10;i++){
            imagetask[i]=new ImageDownloader();
        }

        for(int i=0;i<10;i++){
        try {
            Log.i("Blaaaa2","i="+i);
            myImage[i]=imagetask[i].execute(image[i]).get();
        }catch(Exception e){
            e.printStackTrace();
        }
        }

        ListView listView=findViewById(R.id.listView);
        MyAdapter adapter=new MyAdapter();
        listView.setAdapter(adapter);
    }

}