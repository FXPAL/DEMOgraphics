package com.fxpal.demographics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassifyPictureActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private FrameLayout mSurfaceViewFrame;
    private ImageView mSnapShotImageView;
    private TextView mTextView, mGenderTV, mAgeTV;
    private Camera mCamera;
    private Preview mPreview;
    private long lastClassificationTime = 0;
    private FaceDetector faceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_picture);


        mSurfaceViewFrame = (FrameLayout) findViewById(R.id.imageview);
        mTextView = (TextView) findViewById(R.id.results_box);
        mGenderTV = (TextView) findViewById(R.id.gender_TV);
        mAgeTV = (TextView) findViewById(R.id.age_TV);
        mSnapShotImageView = (ImageView) findViewById(R.id.snapshotIV);

        faceDetector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

//        selectImage(null);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(ClassifyPictureActivity.this, "Retrieve image failed", Toast.LENGTH_SHORT).show();
                return;
            }
//            mSurfaceViewFrame.setImageURI(data.getData());

//            new execute();

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
                        new Intent[]{takePhotoIntent}
                );

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Camera.PreviewCallback previewCallback = new Camera.PreviewCallback()
        {
            public void onPreviewFrame(byte[] data, Camera camera)
            {


                try
                {
                    Camera.Parameters parameters = camera.getParameters();
                    int width = parameters.getPreviewSize().width;
                    int height = parameters.getPreviewSize().height;

                    YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

                    byte[] bytes = out.toByteArray();
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Face> faces = faceDetector.detect(frame);


                    if(faces.size()==0){
                        // No faces!
                        mSnapShotImageView.setImageBitmap(null);
                        mTextView.setText("no face");
                        mAgeTV.setText("");
                        mGenderTV.setText("");
                        return;
                    }

                    Face f = faces.valueAt(0);

                    int imgX = (int)f.getPosition().x;
                    int imgY = (int)f.getPosition().y;
                    int w = Math.min((int)f.getWidth(), bitmap.getWidth()-imgX);
                    int h = Math.min((int)f.getHeight(), bitmap.getHeight()-imgY);

                    Bitmap resizedbitmap = Bitmap.createBitmap(bitmap, imgX, imgY, w, h);


                    mSnapShotImageView.setImageBitmap(resizedbitmap);

                    long now = new Date().getTime();
                    if(lastClassificationTime + 1500 > now)
                        return;


                    Log.d("DEMO", "start classify");
                    lastClassificationTime = now;

//                    Toast.makeText(ClassifyPictureActivity.this, "Got it!", Toast.LENGTH_SHORT).show();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    resizedbitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();

                    new UploadDataTask().execute(byteArray);
                }
                catch(Exception e)
                {
                    Log.d("DEMO", "getFace", e);
                }
            }

        };

        mPreview = new Preview(this, Camera.open(0));
        mCamera.setPreviewCallback(previewCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stopPreviewAndFreeCamera();
        mPreview = null;
//        mCamera.release();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    class UploadDataTask extends AsyncTask<byte[], Void, String>{

        @Override
        protected String doInBackground(byte[]... params) {
            HttpURLConnection urlConnection = null;

            String result = "";

            try {

                URL url = new URL("https://gpu02.paldeploy.com:9999/extractfeatures");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);





//                InputStream in = getContentResolver().openInputStream(data.getData());
//                        IOUtils.copy(in, new Base64OutputStream(urlConnection.getOutputStream(), 0));

                HashMap<String, String> param = new HashMap<>();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                IOUtils.copy(in, baos);
                param.put("image", Base64.encodeToString(params[0], Base64.DEFAULT));


                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(param));
                writer.flush();
                writer.close();
                os.close();

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

//            Toast.makeText(ClassifyPictureActivity.this, result, Toast.LENGTH_SHORT).show();
            mTextView.setText(result);
//                    Toast.makeText(ClassifyPictureActivity.this, result, Toast.LENGTH_SHORT).show();
            try {
                JSONObject resultObj = new JSONObject(result);

                mGenderTV.setText(resultObj.getString("gender"));
                mAgeTV.setText(resultObj.getString("age"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class Preview implements SurfaceHolder.Callback {

        SurfaceView mSurfaceView;
        SurfaceHolder mHolder;
        private List<Camera.Size> mSupportedPreviewSizes;

        Preview(Context context, Camera c) {
            setCamera(c);
            mSurfaceView = new SurfaceView(ClassifyPictureActivity.this);
            mSurfaceViewFrame.addView(mSurfaceView);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = mSurfaceView.getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void setCamera(Camera camera) {
            if (mCamera == camera) { return; }

            stopPreviewAndFreeCamera();

            mCamera = camera;

            if (mCamera != null) {
                List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
                mSupportedPreviewSizes = localSizes;
//                mSurfaceViewFrame.requestLayout();

                try {
                    mCamera.setPreviewDisplay(mHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Important: Call startPreview() to start updating the preview
                // surface. Preview must be started before you can take a picture.
                mCamera.startPreview();
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPreviewSize(mSupportedPreviewSizes.get(0).width, mSupportedPreviewSizes.get(0).height);
            parameters.setPreviewSize(640, 480);
            mSurfaceView.requestLayout();
            mCamera.setParameters(parameters);

            // Important: Call startPreview() to start updating the preview surface.
            // Preview must be started before you can take a picture.
            mCamera.startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            if (mCamera != null) {
                // Call stopPreview() to stop updating the preview surface.
                mCamera.stopPreview();
            }
        }

        /**
         * When this function returns, mCamera will be null.
         */
        private void stopPreviewAndFreeCamera() {

            if (mCamera != null) {
                // Call stopPreview() to stop updating the preview surface.
                mCamera.stopPreview();

                // Important: Call release() to release the camera for use by other
                // applications. Applications should release the camera immediately
                // during onPause() and re-open() it during onResume()).
                mCamera.release();

                mCamera = null;

                mHolder.removeCallback(this);
                mSurfaceViewFrame.removeAllViews();

            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
