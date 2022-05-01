package hope.afterlifeprojects.seif.french.guessthecelebirty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

    ArrayList<String> celebURL = new ArrayList<String>();
    ArrayList<String> celeNames = new ArrayList<String>();
    int chosenCeleb = 0 ;
    ImageView imageView;
    int locationOfCorrectAnswer = 0 ;
    String [] answers = new String[4];
    Button   button0
            ,button1
            ,button2
            ,button3;

    public void celebchosen (View view)
    {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
        {
            Toast.makeText(getApplicationContext() , "Correct" ,Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(getApplicationContext() , "Wrong! it was " + celeNames.get(chosenCeleb) ,Toast.LENGTH_SHORT).show();
        }

        generateQ();
    }

    public class ImageDownloader extends AsyncTask<String , Void , Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url  = new URL(urls [0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }


            return null;
        }
    }



    public class DownLoadTask extends AsyncTask<String , Void ,String> {

        @Override
        protected String doInBackground(String... urls) {

            StringBuilder result = new StringBuilder();
            URL url ;
            HttpURLConnection connection = null ;


            try {

                url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1)
                {
                   char current = (char) data ;
                    result.append(current);
                    data=  reader.read();
                }

                return result.toString();

            }

            catch (Exception e ) {

                e.printStackTrace();
            }


            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownLoadTask task = new DownLoadTask();
        String result = null ;
        imageView = (ImageView)findViewById(R.id.imageViewS);
        button0 = (Button)findViewById(R.id.button1);
        button1 = (Button)findViewById(R.id.button2);
        button2 = (Button)findViewById(R.id.button3);
        button3 = (Button)findViewById(R.id.button4);


        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String [] spiltResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(spiltResult[0]);

            while (m.find())
            {
                celebURL.add(m.group(1));
            }


             p = Pattern.compile("alt=\"(.*?)\"");
             m = p.matcher(spiltResult[0]);

            while (m.find())
            {
                celeNames.add(m.group(1));
            }




            Log.i("Content of URl" ,result );
        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();
        }

        generateQ();
    }

    public void generateQ()
    {
        Random rand = new Random();
        chosenCeleb = rand.nextInt(celebURL.size());

        ImageDownloader imgeTask  = new ImageDownloader();

        Bitmap celebImage ;

        try {
            celebImage = imgeTask.execute(celebURL.get(chosenCeleb)).get();

            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = rand.nextInt(4);
            int inCorrectAnserLocation  ;

            for (int i = 0 ; i < 4 ; i++)
            {
                if (i == locationOfCorrectAnswer)
                {
                    answers[i] = celeNames.get(chosenCeleb);
                }else
                {
                    inCorrectAnserLocation = rand.nextInt(celebURL.size());

                    while (inCorrectAnserLocation == chosenCeleb)
                    {
                        inCorrectAnserLocation = rand.nextInt(celebURL.size());
                    }

                    answers[i] = celeNames.get(inCorrectAnserLocation);


                }
            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
