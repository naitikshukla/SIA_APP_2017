package com.teamiss.sia.siacargomanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraListener;
import com.wonderkiln.camerakit.CameraView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.button1) Button capture;
    @InjectView(R.id.button2) Button reCapture;
    @InjectView(R.id.button3) Button button;
    @InjectView(R.id.camera) CameraView camera;
    @InjectView(R.id.img) ImageView preview;
    @InjectView(R.id.height)
    TextView height;
    @InjectView(R.id.width)
    TextView width;
    Integer numCargo;
    Bitmap result;
    View rellayout ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        result=null;
        preview.setVisibility(View.GONE);
        Intent i = getIntent();
        numCargo = i.getIntExtra("numCargo",1);
        camera.setMethod(CameraKit.Constants.METHOD_STANDARD);
        rellayout = findViewById(R.id.r1);
        rellayout.setVisibility(View.GONE);
        camera.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);
                // Create a bitmap
                result = BitmapFactory.decodeByteArray(picture, 0, picture.length);

                camera.stop();
                            }
        });

        capture.setOnClickListener(this);
        reCapture.setOnClickListener(this);
        button.setOnClickListener(this);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        camera.stop();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent firstView = new Intent(this, StartingActivity.class);
        startActivity(firstView);
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view == capture)
            camera.captureImage();
        else if(view == reCapture) {
            camera.start();
            preview.setVisibility(View.GONE);
            rellayout.setVisibility(View.GONE);
            camera.setVisibility(View.VISIBLE);
        }
        else if(view == button){
            if(result != null) {
                //Bitmap img = Bitmap.createScaledBitmap(result, 100, 100, true);
                Bitmap img = getResizedBitmap(result, 200);
                String encodedImage = toBase64(img);

                new CommunicateWithServer(MainActivity.this).execute(encodedImage);
            }
        }
    }

    private String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return (Base64.encodeToString(byteArray, Base64.DEFAULT)).replace("\n", "");
    }

    public void uploadDone(String string){
        if(string != null) {
            try {
                JSONObject resObj = new JSONObject(string);
                String wid = resObj.getString("width");
                String ht = resObj.getString("height");
                String image = resObj.getString("image");
                //Log.d("test", string);
                preview.setVisibility(View.VISIBLE);
                rellayout.setVisibility(View.VISIBLE);
                height.setText(ht.substring(0,4)+" in");
                width.setText(wid.substring(0,4)+" in");
                camera.setVisibility(View.GONE);
                byte[] picture = Base64.decode(image, Base64.DEFAULT);
                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                preview.setImageBitmap(result);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, "Measurement did not happen. Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
