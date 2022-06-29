package com.saad.genderandemotionrecognizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    Button playMusic;
    String audioPath = "";
    ImageButton recordAudio;

    public static MultipartBody.Part fileRequest(File file, String image) {
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                file);
        return MultipartBody.Part.createFormData(image, file.getName(), fileReqBody);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playMusic = findViewById(R.id.playMusic);
        recordAudio = findViewById(R.id.recordButton);
        if (isMicrophonePresent()) {
            checkWritePermission();
            if (!checkWritePermission()) {
                requestWritePermission();
            }
        }
//        String filepath = Environment.getExternalStorageDirectory().getPath();
//        File file = new File(filepath,"GenderRecognition");
//        if(!file.exists()){
//            file.mkdirs();
//        }


        wavClass wavObj = new wavClass(getRecordingFilePath());


        recordAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (checkWritePermission()) {
                            Toast.makeText(getApplicationContext(), "Recording Start", Toast.LENGTH_LONG).show();
                            wavObj.startRecording();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (checkWritePermission()) {
//                            Toast.makeText(getApplicationContext(),"Stop Recording",Toast.LENGTH_LONG).show();
                            audioPath = wavObj.stopRecording();
                            uploadFile();
                            Toast.makeText(getApplicationContext(), "Request Send", Toast.LENGTH_LONG).show();

                        }
                        break;

                }
                return false;
            }
        });
//        startbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(checkWritePermission()) {
//                    Toast.makeText(getApplicationContext(),"Recording Start",Toast.LENGTH_LONG).show();
//                    wavObj.startRecording();
//                }
//                if(!checkWritePermission()){
//                    requestWritePermission();
//                }
//            }
//        });

//        stopbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String pathh = wavObj.stopRecording();
//                Toast.makeText(getApplicationContext(),"Recording Stoped",Toast.LENGTH_LONG).show();
//                Log.d("Hello", "stopRecording: "+pathh);
//
//            }
//        });
        playMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wavObj.playMusic();
                Toast.makeText(getApplicationContext(), "Music Played", Toast.LENGTH_LONG).show();

            }
        });


    }


    private boolean isMicrophonePresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        }
        return false;
    }


    private boolean checkWritePermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestWritePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        return musicDir.getPath();
    }

    private void uploadFile() {
        // create upload service client
        try {

            UserClient service =
                    ServiceGenerator.getRetrofit().create(UserClient.class);

            
            // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
            // use the FileUtils to get the actual file by uri
            File file = new File(audioPath);

            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse(getContentResolver().getType(Uri.fromFile(file))), file);

            // MultipartBody.Part is used to send also the actual file name
            Log.i("TAG", "uploadFile: ");
            MultipartBody.Part body =
                    MainActivity.fileRequest(file, "myAudio");

            // add another part within the multipart request
            String descriptionString = "hello, this is description speaking";
            RequestBody description =
                    RequestBody.create(
                            okhttp3.MultipartBody.FORM, descriptionString);

            // finally, execute the request
            Log.i("TAG", "uploadFile: BEFORE SENDING IN QUEUE");

            Call<ResponseBody> call = service.upload(description, body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       Response<ResponseBody> response) {
                    Log.v("Upload", "success");
                    Toast.makeText(getApplicationContext(), "Upload", Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Upload error:", t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.i("TAG", "uploadFile: EXCEPTION " + e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }


}