package com.fxpal.demographics;

import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64OutputStream;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClassifyPictureActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private ImageView mImageView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_picture);


        mImageView = (ImageView) findViewById(R.id.imageview);
        mTextView = (TextView) findViewById(R.id.results_box);

        selectImage(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(ClassifyPictureActivity.this, "Retrieve image failed", Toast.LENGTH_SHORT).show();
                return;
            }
            mImageView.setImageURI(data.getData());

            new AsyncTask<Void, Void, String>(){

                @Override
                protected String doInBackground(Void... params) {
                    HttpURLConnection urlConnection = null;
                    
                    String result = "";

                    try {

                        URL url = new URL("http://gpu02.fxpal.net:8080/extractfeatures");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        InputStream in = getContentResolver().openInputStream(data.getData());
                        IOUtils.copy(in, new Base64OutputStream(urlConnection.getOutputStream(), 0));

                        result =  IOUtils.toString(urlConnection.getInputStream());


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        if(urlConnection != null)
                            urlConnection.disconnect();
                    }

                    return result;
                }

                @Override
                protected void onPostExecute(String result) {

                    mTextView.setText(result);
//                    Toast.makeText(ClassifyPictureActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            }.execute();

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...


        }
    }

    public void selectImage(View view){
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[] { takePhotoIntent }
                );

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }
}
