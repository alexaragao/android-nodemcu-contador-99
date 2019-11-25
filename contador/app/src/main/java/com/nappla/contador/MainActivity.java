package com.nappla.contador;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private final String baseUrl = "http://192.168.43.156:8080";

    private int contagem = 0;
    private TextView tvContagem;
    private SeekBar seekbar;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#1587a3\">" + getString(R.string.app_name) + "</font>"));

        tvContagem = (TextView) findViewById(R.id.tvContagem);
        seekbar = (SeekBar) findViewById(R.id.seek_bar);
        seekbar.setOnTouchListener((v, event) -> true);

        Button btnIncrease = (Button)findViewById(R.id.btnIncrease);
        btnIncrease.setOnClickListener(v -> sendIncreaseRequest());

        Button btnDecrease = (Button) findViewById(R.id.btnDecrease);
        btnDecrease.setOnClickListener(v -> sendDecreaseRequest());

        Button btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> sendRestartRequest());

        final Runnable r = new Runnable() {
            public void run() {
                sendUpdateRequest();
                handler.postDelayed(this, 3modu00);
            }
        };

        handler.postDelayed(r, 300);
    }

    private void sendUpdateRequest() {
        new StartRequest().execute("");
    }

    private void sendRestartRequest() {
        new StartRequest().execute("reset");
    }

    private void sendDecreaseRequest() {
        new StartRequest().execute("decrease");
    }

    private void sendIncreaseRequest() {
        new StartRequest().execute("increase");
    }

    protected void updateScreenContagem(String contagem) {
        Integer cont = Integer.valueOf(contagem.replaceAll("[^0-9]",""));
        seekbar.setProgress(cont);
        tvContagem.setText(contagem);
    }


    class StartRequest extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection;
            String req = params[0];
            String response = null;
            try {
                URL url = new URL(baseUrl + "/" + req);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.d("TESTE", urlConnection.getInputStream().toString());
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String linha;
                StringBuffer stringBuffer = new StringBuffer();
                while ((linha = reader.readLine()) != null) {
                    stringBuffer.append(linha);
                    stringBuffer.append("\n");
                }
                response = stringBuffer.toString();
//                Log.d("TESTE", stringBuffer.toString());
//                inputStream.close();
                urlConnection.disconnect();
            } catch (IOException e) { System.out.println(e.toString()); }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                updateScreenContagem(s);
            }
            System.out.println(s);
            Log.d("TESTE", "D");
        }
    }
}
