package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ai_webza_tec.ai_method;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static com.example.ai_webza_tec.ai_method.checkForPreviousCallList;
import static com.example.ai_webza_tec.ai_method.clearContactListSavedData;
import static com.example.ai_webza_tec.ai_method.getContactList;
import static com.example.ai_webza_tec.ai_method.makeCall;
import static com.example.ai_webza_tec.ai_method.makeCallFromSavedContactList;
import static com.example.myapplication.Function.fetchName;
import static com.example.myapplication.Function.wishMe;

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    private TextView textView;
    private TextView textview2;
    private TextToSpeech tts;
    private boolean animationOn = false;

    LottieAnimationView lottieAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState)//When an Activity first call or launched then this method is responsible to create the activity.
    {
        super.onCreate(savedInstanceState);//run your code in addition to the existing code in the onCreate() of the parent class.
        setContentView(R.layout.activity_main); //sets the XML file as you want as your main layout when the app starts
        findById();
        initializeTextToSpeech();
        initializeResult();


        lottieAnimation = findViewById(R.id.lottieAnimation);

        lottieAnimation.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   if (animationOn) {
                                                       lottieAnimation.setMinAndMaxProgress(0.5f, 1.0f);
                                                       lottieAnimation.playAnimation();
                                                       animationOn = false;
                                                   } else {
                                                       lottieAnimation.setMinAndMaxProgress(0.0f, 0.5f);
                                                       lottieAnimation.playAnimation();
                                                       animationOn = true;
                                                   }
                                                   intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//The constant ACTION_RECOGNIZE_SPEECH starts an activity that will prompt the user for speech and send it through a speech recognizer.
                                                   intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, //Informs the recognizer which speech model to prefer when performing ACTION_RECOGNIZE_SPEECH.
                                                           RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //Use a language model based on free-form speech recognition
                                                   intentRecognizer.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                                                   speechRecognizer.startListening(intentRecognizer);
                                               }

                                           });
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED); //To grant permission to use microphone





    }

    private void initializeTextToSpeech() {
        tts= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {  //called to signal the completion of the TextToSpeech engine initialization.
                if (tts.getEngines().size()==0) {
                    Toast.makeText(MainActivity.this, "Engine is not available", Toast.LENGTH_SHORT).show();
                }else{
                    String s = wishMe();//wishMe function to greet by time
                    speak("Hii I am ..."+s);

                }
            }
        });
    }

    private void speak(String msg) {
        textview2.setText(msg);
        //// Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(msg,TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(msg,TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void findById(){
        textView = (TextView)findViewById(R.id.textView);
        textview2= (TextView)findViewById(R.id.textView2);
    }
    private void initializeResult() {
        if (SpeechRecognizer.isRecognitionAvailable( this)){
           speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
           speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {

            }  //called when the endpointer is ready for the user to start speaking

            @Override
            public void onBeginningOfSpeech() {

            } //the user has started speaking

            @Override
            public void onRmsChanged(float rmsdB) {

            }  //the sound level in the audio stream has changed

            @Override
            public void onBufferReceived(byte[] buffer) {

            }  //more sound has been recieved

            @Override
            public void onEndOfSpeech() {

            }  //Called after the user stops speaking

            @Override
            public void onError(int error) {

            }  //A network or recognition error occured

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Toast.makeText(MainActivity.this, ""+result.get(0), Toast.LENGTH_SHORT).show();
                textView.setText(result.get(0));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    responce(result.get(0));
                } //Called when recognition results are ready
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            } //Reserved for adding future eevents
        });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void responce(String msg) { //responce we get from tts
        String msgs = msg.toLowerCase();
        if (msgs.indexOf("hi")!= -1){
            speak( "hello ! how are you?");

        }
        if (msgs.indexOf("hello")!= -1){
            speak( "hello ! how are you?");

        }else if (msgs.indexOf("i am not fine")!= -1){
            speak("please take care");
        }
        if (msgs.indexOf("what")!= -1){
            if (msgs.indexOf("your")!= -1){
                if (msgs.indexOf("name")!= -1){
                    speak( "My name is ");
                }//tells the name
            }
            if(msgs.indexOf("time")!= -1){
                if(msgs.indexOf("now")!= -1){
                    Date date = new Date();
                    String time = DateUtils.formatDateTime(this,date.getTime(),DateUtils.FORMAT_SHOW_TIME);
                    speak("The time now is"+time);
                }//tells currunt time
            }

            if(msgs.indexOf("today")!= -1){
                if(msgs.indexOf("date")!= -1){
                    SimpleDateFormat df = new  SimpleDateFormat("dd MMMM yyyy");
                    Calendar cal = Calendar.getInstance();
                    String todays_date = df.format(cal.getTime());
                    speak("the today's date is "+todays_date);
                }//tells currunt date
            }
        }
        if(msgs.indexOf("open")!= -1){
            if(msgs.indexOf("google")!= -1){
                Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));//to open youtube
                startActivity(intent);
            }

            if(msgs.indexOf("youtube")!= -1){
                Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));//to open youtube
                startActivity(intent);
            }
            if(msgs.indexOf("facebook")!= -1){
                Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));// To open faceboock webpage
                startActivity(intent);
            }
            if(msgs.indexOf("whatsapp")!= -1){
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.whatsapp");//to open whatsapp
                ctx.startActivity(i);
            }
        }

        if(msgs.indexOf("call")!= -1){
            final String[] listname ={""};
            final String name = fetchName(msgs);
            Log.d("Name",name);

            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CALL_PHONE
                    ).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        if (checkForPreviousCallList(MainActivity.this)) {
                            speak(makeCallFromSavedContactList(MainActivity.this, name));
                        } else {
                            HashMap<String, String> list = getContactList(MainActivity.this, name);
                            if (list.size() > 1) {
                                for (String i : list.keySet()) {
                                    listname[0] = listname[0].concat("..........................!" + i);
                                }
                                speak("Which one sir ? .. There is " + listname[0]);
                            } else if (list.size() == 1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                    makeCall(MainActivity.this, list.values().stream().findFirst().get());
                                    clearContactListSavedData(MainActivity.this);
                                }
                            } else {
                                speak("NO CONTACT FOUND");
                                clearContactListSavedData(MainActivity.this);
                            }
                        }
                    }
                    if (report.isAnyPermissionPermanentlyDenied()) {
                    }
                }
                @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();}

            }).check();
        }



    }



}













