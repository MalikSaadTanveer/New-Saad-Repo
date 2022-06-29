package com.saad.genderandemotionrecognizer;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.saad.genderandemotionrecognizer.mvvm.MvvmUtils;
import com.saad.genderandemotionrecognizer.mvvm.capsules.response.PredictionResponse;
import com.saad.genderandemotionrecognizer.mvvm.mapping_utils.GenericCall;
import com.saad.genderandemotionrecognizer.mvvm.mapping_utils.GenericResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


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


        recordAudio.setOnTouchListener((view, motionEvent) -> {
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

                    }
                    break;
            }
            return false;
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
        Toast.makeText(this, "Sending Request", Toast.LENGTH_SHORT).show();
        new GenericCall<>(MvvmUtils.getNcs().upload(fileRequest(new File(audioPath), "myAudio")))
                .getMutableLiveData().observe(this, this::initResponse);
    }

    private void initResponse(GenericResponse<PredictionResponse> userPojoGenericResponse) {
        Toast.makeText(this, "Response attained", Toast.LENGTH_SHORT).show();
        if (!userPojoGenericResponse.isSuccessful()) {
            MvvmUtils.printGeneralErrors(this, userPojoGenericResponse.getErrorMessages());
        } else {
            Toast.makeText(this, "File uploaded to server", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, userPojoGenericResponse.getResponse().toString(), Toast.LENGTH_SHORT).show();
            Log.i("TAG", "initResponse: " + userPojoGenericResponse.getResponse().getEmotion());
            Log.i("TAG", "initResponse: " + userPojoGenericResponse.getResponse().getGender());
            Log.i("TAG", "initResponse: " + userPojoGenericResponse.getResponse().getFemaleProb());
            Log.i("TAG", "initResponse: " + userPojoGenericResponse.getResponse().getMale_prob());
        }
    }
}