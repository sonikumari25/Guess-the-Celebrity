package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebURLs=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    int chosenCeleb=0;
    ImageView imageView;
    int locationOfCorrectCele;
    Random rand;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    TextView textView;
    String[] answer=new String[4];
    String correctAns;
    public void displayQuestion() {
        int incorrectCelebLoc;
        rand = new Random();
        chosenCeleb=rand.nextInt(celebURLs.size());
        ImageDownloader imageTask=new ImageDownloader();
        Bitmap celeImage;
        try {
            celeImage=imageTask.execute(celebURLs.get(chosenCeleb)).get();
            imageView.setImageBitmap(celeImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        locationOfCorrectCele = rand.nextInt(4);
        for (int i = 0; i < 4; i++) {

            if (i==locationOfCorrectCele) {
                answer[i] = celebNames.get(chosenCeleb);
            }
            else {
                incorrectCelebLoc=rand.nextInt(celebURLs.size());
                while(incorrectCelebLoc==chosenCeleb){
                    incorrectCelebLoc=rand.nextInt(celebURLs.size());
                }
                answer[i]=celebNames.get(incorrectCelebLoc);
            }

        }
        correctAns=celebNames.get(chosenCeleb);
        button0.setText(answer[0]);
        button1.setText(answer[1]);
        button2.setText(answer[2]);
        button3.setText(answer[3]);
    }
    public void answer(View view){
     if(view.getId()==R.id.button0 && answer[0].equals(correctAns)==true){
         textView.setText("Correct!");
         textView.setVisibility(View.VISIBLE);
     }
        else if(view.getId()==R.id.button1 && answer[1].equals(correctAns)==true){
            textView.setText("Correct!");
            textView.setVisibility(View.VISIBLE);
        }
        else if(view.getId()==R.id.button2 && answer[2].equals(correctAns)==true){
            textView.setText("Correct!");
            textView.setVisibility(View.VISIBLE);
        }
        else if(view.getId()==R.id.button3 && answer[3].equals(correctAns)==true){
            textView.setText("Correct!");
            textView.setVisibility(View.VISIBLE);
        }
        else{
            textView.setText("Incorrect! "+"The correct name is "+celebNames.get(chosenCeleb));
            textView.setVisibility(View.VISIBLE);
        }
        displayQuestion();
        //textView.setVisibility(View.INVISIBLE);
    }
    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream=connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result=new StringBuilder();
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(urls[0]);
               urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while (data!=-1){
                    char current= (char)data;
                    result.append(current);
                    data=reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.imageView);
        button0=(Button)findViewById(R.id.button0);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        textView=(TextView)findViewById(R.id.textView);
        textView.setVisibility(View.INVISIBLE);
        DownloadTask task=new DownloadTask();
        String result=null;
        try{
            result=task.execute("https://www.imdb.com/list/ls022431524/").get();
            String[] splitResult=result.split("<div class=\"desc lister-total-num-results\">");
            Pattern p=Pattern.compile("src=\"(.*?)\"");
            Matcher m=p.matcher(splitResult[1]);
            int count=1;
            while (m.find() && count<=100){
                celebURLs.add(m.group(1));
                Log.i("URL "+String.valueOf(count),m.group(1));
                count++;

            }
            p=Pattern.compile("img alt=\"(.*?)\"");
            m=p.matcher(splitResult[1]);
            count=1;
            while (m.find() && count<=100){
                celebNames.add(m.group(1));
                Log.i("Celebrity"+String.valueOf(count),m.group(1));
                count++;

            }
            displayQuestion();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}