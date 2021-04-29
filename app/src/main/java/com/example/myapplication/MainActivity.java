package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static com.example.myapplication.Function.wishMe;

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    private TextView textView;
    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState)//When an Activity first call or launched then this method is responsible to create the activity.
    {
        super.onCreate(savedInstanceState);//run your code in addition to the existing code in the onCreate() of the parent class.
        setContentView(R.layout.activity_main); //sets the XML file as you want as your main layout when the app starts
        findById();
        initializeTextToSpeech();
        initializeResult();

        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED); //To grant permission to use microphone





    }

    private void initializeTextToSpeech() {
        tts= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (tts.getEngines().size()==0) {
                    Toast.makeText(MainActivity.this, "Engine is not available", Toast.LENGTH_SHORT).show();
                }else{
                    String s = wishMe();
                    speak("Hii I am Shreya..."+s);

                }
            }
        });
    }

    private void speak(String msg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(msg,TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(msg,TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void findById(){
        textView = (TextView)findViewById(R.id.textView);

    }
    private void initializeResult() {
        if (SpeechRecognizer.isRecognitionAvailable( this)){
           speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
           speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Toast.makeText(MainActivity.this, ""+result.get(0), Toast.LENGTH_SHORT).show();
                textView.setText(result.get(0));
                responce(result.get(0));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        }
    }

    private void responce(String msg) {
        String msgs = msg.toLowerCase();
        if (msgs.indexOf("hi")!= -1){
            speak( "hello sir ! how are you?");

        }else if (msgs.indexOf("i am not fine")!= -1){
            speak("please take care");
        }
    }


    public void Record(View view) {
        intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//The constant ACTION_RECOGNIZE_SPEECH starts an activity that will prompt the user for speech and send it through a speech recognizer.
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, //Informs the recognizer which speech model to prefer when performing ACTION_RECOGNIZE_SPEECH.
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //Use a language model based on free-form speech recognition
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        speechRecognizer.startListening(intentRecognizer);
    }

}