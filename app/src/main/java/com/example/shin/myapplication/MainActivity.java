package com.example.shin.myapplication;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class MainActivity extends AppCompatActivity {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceViewHolder;
    private Handler mHandler;
    private ImageReader mImageReader;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mSession;
    private int mDeviceRotation;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private SensorManager mSensorManager;
    private DeviceOrientation deviceOrientation;
    int mDSI_height, mDSI_width;
    public RadioGroup rg;
    public LinearLayout ll;
    public RelativeLayout L1;
    public LinearLayout L2;
    public ConstraintLayout C1;
    public HorizontalScrollView hs;
    public ArrayList<String> imgsrc;
    public ArrayList<Bitmap> imgBit;
    public ArrayList<Bitmap> imgBit1;
    public ArrayList<File> imgFile;
    public String subject = "";
    public String sender = "";
    public String recipient = "";
    public String user = "noreply3875@gmail.com";
    private String passwd = "AEjHL4NerXmmo1WG8aPs_Cebc5ZWTkEHwI37IywIbbwuL22rQrl-LV93YFUPyxRDU0rNrd1QWlq-VxAWOjno-HR-m-RBzOFHeg";
    private MailVO vo;
    EditText sender1 ;
    EditText recipent1;
    EditText subject1;
    EditText content1;
    EditText filename1;
    TextView tempFileText;
    private SurfaceView sf;
    private HorizontalScrollView hs1;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    DBHelper dbHelper;
    MediaActionSound mediaActionSound;
    static {
        ORIENTATIONS.append(ExifInterface.ORIENTATION_NORMAL, 0);
        ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_90, 90);
        ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_180, 180);
        ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_270, 270);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaActionSound = new MediaActionSound();

        // 상태바를 안보이도록 합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 화면 켜진 상태를 유지합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        imgsrc = new ArrayList<>();
        setContentView(R.layout.activity_main);
        sender1 = findViewById(R.id.sender);
        recipent1 = findViewById(R.id.recipent);
        subject1 = findViewById(R.id.subject);
        content1 = findViewById(R.id.content);
        filename1 = findViewById(R.id.fileName);
        tempFileText = findViewById(R.id.tempFileText);
        sf = findViewById(R.id.surfaceView);
        hs1 = findViewById(R.id.hs);
        Button button = findViewById(R.id.take_photo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mediaActionSound.play(MediaActionSound.SHUTTER_CLICK);
                takePicture();
            }
        });
        filename1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String temp = "";
                for(int i=1;i<=imgsrc.size();i++) {
                    temp +="[" + filename1.getText() + String.valueOf(i) + ".jpg] ";
                }
                tempFileText.setHint(temp);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = "";
                for(int i=1;i<=imgsrc.size();i++) {
                    temp +="[" + filename1.getText() + String.valueOf(i) + ".jpg] ";
                }
                tempFileText.setHint(temp);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());
        ll = findViewById(R.id.ll);
        mSurfaceView = findViewById(R.id.surfaceView);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        deviceOrientation = new DeviceOrientation();
        vo = new MailVO();
        try {
            dbHelper = new DBHelper(getApplicationContext(), "SYDB.db", null, 1);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"메일 전송 시도 후 오류 시 문의주세요.",Toast.LENGTH_SHORT).show();
        }
        dbHelper.delete();
        imgBit = new ArrayList<>();
        imgBit1 = new ArrayList<>();
        imgFile = new ArrayList<>();
        hs = findViewById(R.id.hs);
        L1 = findViewById(R.id.L1);
        L2 = findViewById(R.id.L2);
        C1 = findViewById(R.id.c1);
        rg = findViewById(R.id.rg);
        initSurfaceView();

        set_mail_content();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(deviceOrientation.getEventListener(), mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(deviceOrientation.getEventListener(), mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(deviceOrientation.getEventListener());
    }

    public void initSurfaceView() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDSI_height = displayMetrics.heightPixels;
        mDSI_width = displayMetrics.widthPixels;


        mSurfaceViewHolder = mSurfaceView.getHolder();
        mSurfaceViewHolder.setFixedSize(mDSI_width,mDSI_height/2);
        mSurfaceViewHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCameraAndPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                if (mCameraDevice != null) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }


        });
    }
    //삭제후 삭제 처리를 위한 새로고침
    private void galleryAddPic(String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
        File f = new File(currentPhotoPath); //새로고침할 사진경로
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @TargetApi(19)
    public void initCameraAndPreview() {
        HandlerThread handlerThread = new HandlerThread("CAMERA2");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        Handler mainHandler = new Handler(getMainLooper());
        try {
            String mCameraId = "" + CameraCharacteristics.LENS_FACING_FRONT; // 후면 카메라 사용

            CameraManager mCameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            Size largestPreviewSize = map.getOutputSizes(ImageFormat.JPEG)[0];
            Log.i("LargestSize", largestPreviewSize.getWidth() + " " + largestPreviewSize.getHeight());

            //setAspectRatioTextureView(largestPreviewSize.getHeight()/2, largestPreviewSize.getWidth());

            mImageReader = ImageReader.newInstance(largestPreviewSize.getWidth(), largestPreviewSize.getHeight(), ImageFormat.JPEG,/*maxImages*/7);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mainHandler);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mCameraManager.openCamera(mCameraId, deviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            Toast.makeText(this, "카메라를 열지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            int size = 1;
            switch (Integer.valueOf(rg.getCheckedRadioButtonId())%10){
                case 2:
                    break;
                case 4: size = 4;
                    break;
                case 7: size = 2;
                    break;
            }
            System.out.println("asd "+size);
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = size;
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, op);
            op.inSampleSize = 4;
            final Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, op);
            //setImage(bitmap);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    imgBit.add(bitmap);
                    imgBit1.add(bitmap1);

                    new SaveImageTask().execute(bitmap);
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    Bitmap originalBm;
//                    originalBm = BitmapFactory.decodeFile(imgsrc.get(imgsrc.size() - 1), options);
//                    File file = new File(imgsrc.get(imgsrc.size() - 1));
//                    imgFile.add(file);
                                //Check
                }
            }).start();
            image.close();
            image.close();
        }
    };


    private CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            try {
                takePreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Toast.makeText(MainActivity.this, "카메라를 열지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    };


    public void takePreview() throws CameraAccessException {
        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        mPreviewBuilder.addTarget(mSurfaceViewHolder.getSurface());
        mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceViewHolder.getSurface(), mImageReader.getSurface()), mSessionPreviewStateCallback, mHandler);
    }

    private CameraCaptureSession.StateCallback mSessionPreviewStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mSession = session;

            try {

                mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                mSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Toast.makeText(MainActivity.this, "카메라 구성 실패", Toast.LENGTH_SHORT).show();
        }
    };

    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            mSession = session;
            unlockFocus();
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            mSession = session;
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    };


    public void takePicture() {

        try {
            CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);


            // 화면 회전 안되게 고정시켜 놓은 상태에서는 아래 로직으로 방향을 얻을 수 없어서
            // 센서를 사용하는 것으로 변경
            //deviceRotation = getResources().getConfiguration().orientation;
            mDeviceRotation = ORIENTATIONS.get(deviceOrientation.getOrientation());

            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mDeviceRotation);
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            mSession.capture(mCaptureRequest, mSessionCaptureCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) throws Exception {
        if (bitmap == null) return null;
        if (degrees == 0) return bitmap;

        Matrix m = new Matrix();
        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }


    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            mSession.capture(mPreviewBuilder.build(), mSessionCaptureCallback,
                    mHandler);
            // After this, the camera will go back to the normal state of preview.
            mSession.setRepeatingRequest(mPreviewBuilder.build(), mSessionCaptureCallback,
                    mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * A copy of the Android internals  insertImage method, this method populates the
     * meta data with DATE_ADDED and DATE_TAKEN. This fixes a common problem where media
     * that is inserted manually gets saved at the end of the gallery (because date is not populated).
     *
     * @see android.provider.MediaStore.Images.Media#insertImage(ContentResolver, Bitmap, String, String)
     */
    public final String insertImage(ContentResolver cr,
                                    Bitmap source,
                                    String title,
                                    String description) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        //values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "hahaha");
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.DESCRIPTION, "descript");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            //Uri temp = Uri.parse("/shin/");
            //url = cr.insert(temp, values);
            imgsrc.add(getPath(url));
            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void send_email(View view) {
        vo.setSubject(subject1.getText().toString());
        vo.setContent(content1.getText().toString());
        vo.setFileName(filename1.getText().toString());
        sending();
    }
    public boolean checkEmail(String email){
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
    }
    public void sending(){
        try {
            //GMailSender.sendMail(제목, 본문내용, 받는사람);), options);
            if (sender1.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "보내는 사람을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!checkEmail(recipent1.getText().toString())){
                Toast.makeText(getApplicationContext(), "받는 사람을 정상적으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            GMailSender gMailSender = new GMailSender(user, passwd);
            gMailSender.sendMail(imgFile, vo);
        } catch (SendFailedException e) {
            Toast.makeText(getApplicationContext(), "받는 사람을 정상적으로 입력해주세요.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "사진을 먼저 찍어주세요.", Toast.LENGTH_SHORT).show();
        }
        imgBit.clear();
        imgBit1.clear();
        imgFile.clear();
        for (int i = 0; i < imgsrc.size(); i++) {
            File f = new File(imgsrc.get(i));
            f.delete();
        }
        imgsrc.clear();
        ll.removeAllViews();
        Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
        //finish();
    }
    public void set_mail(View view) {
        set_mail_content();
        L1.setVisibility(View.GONE);
        L2.setVisibility(View.VISIBLE);
    }

    public void save_content(View v) {
        vo.setSender(sender1.getText().toString());
        vo.setRecipent(recipent1.getText().toString());
        vo.setContent(content1.getText().toString());
        vo.setSubject(subject1.getText().toString());
        //vo.setFileName(filename1.getText().toString());
        dbHelper.update(vo);
        //Toast.makeText(getApplicationContext(),"저장되었습니다.",Toast.LENGTH_SHORT).show();

        L1.setVisibility(View.VISIBLE);
        L2.setVisibility(View.GONE);
    }
    public void set_mail_content(){
        MailVO v = dbHelper.getResult();
        sender1.setText(v.getSender());
        recipent1.setText(v.getRecipent());
       // subject1.setText(v.getSubject());
        content1.setText(v.getContent());
        String temp = "";
        for(int i=1;i<=imgsrc.size();i++) {
            temp +="[" + filename1.getText() + String.valueOf(i) + ".jpg] ";
        }
        tempFileText.setHint(temp);
       // filename1.setText(v.
    }

    public void send_email2(View view) {
        save_content(view);
        L1.setVisibility(View.VISIBLE);
        L2.setVisibility(View.GONE);
        set_mail_content();
        send_email(view);
    }

    public void appStart(View view) {
        L1.setVisibility(View.VISIBLE);
        L2.setVisibility(View.GONE);
        C1.setVisibility(View.GONE);
    }

    public void clear_photo(View view) {
        imgBit.clear();
        imgBit1.clear();
        imgFile.clear();
        for (int i = 0; i < imgsrc.size(); i++) {
            File f = new File(imgsrc.get(i));
            f.delete();
        }
        imgsrc.clear();
        ll.removeAllViews();
    }
    private class SaveImageTask extends AsyncTask<Bitmap, Void, Void> {


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "사진을 저장하였습니다.", Toast.LENGTH_SHORT).show();
            setImage();
        }

        @Override
        protected Void doInBackground(Bitmap... data) {
            FileOutputStream outStream = null;

            Bitmap bitmap = null;
            try {
                bitmap = getRotatedBitmap(data[0], mDeviceRotation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            insertImage(getContentResolver(), bitmap, "zxcv" + System.currentTimeMillis(), "asdf");

            return null;
        }


    }
    // 출처 https://stackoverflow.com/a/43516672

    private void setAspectRatioTextureView(int ResolutionWidth, int ResolutionHeight) {
        if (ResolutionWidth > ResolutionHeight) {
            int newWidth = mDSI_width;
            int newHeight = ((mDSI_width * ResolutionWidth) / ResolutionHeight);
            updateTextureViewSize(newWidth, newHeight);

        } else {
            int newWidth = mDSI_width;
            int newHeight = ((mDSI_width * ResolutionHeight) / ResolutionWidth);
            updateTextureViewSize(newWidth, newHeight);
        }

    }

    private void updateTextureViewSize(int viewWidth, int viewHeight) {
        Log.d("@@@", "TextureView Width : " + viewWidth + " TextureView Height : " + viewHeight);
        //mSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(viewWidth/2, viewHeight));
    }

    public void setImage() {
        Iterator<Bitmap> iter;
        ImageView imageView;
        ll.removeAllViews();
        if ((iter = imgBit1.iterator()) != null)
            for (int i = 0; i < imgBit1.size(); i++) {
                try {
                    imageView = new ImageView(this);
                    imageView.setPadding(10, 10, 0, 10);
                    imageView.setLayoutParams(new LayoutParams(hs.getHeight(), hs.getHeight()));
                    imageView.setImageBitmap(getRotatedBitmap(imgBit1.get(i), mDeviceRotation));
                    ll.addView(imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        try {
            File file = new File(imgsrc.get(imgsrc.size() - 1));
            imgFile.add(file);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"예상치 못한 내부 오류 발생\n이미지를 삭제하셨습니까?",Toast.LENGTH_SHORT).show();
        }
    }

}