package com.dating.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class SignUpActivity extends AppCompatActivity {
    Button choose_image;
    CircleImageView profileImg;
    RelativeLayout rl_capture,rl_gallery;
    public String PickedImgPath = null;
    final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE = 2;
    String imagePath,profileString;
    Toolbar toolbar;

    public static String getEncodedImage(Bitmap bitmapImage) {
        ByteArrayOutputStream baos;
        baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        String encodedImagePatientImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImagePatientImage;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        try {
            toolbar = findViewById(R.id.toolbar);
            choose_image = findViewById(R.id.choose_image);
            profileImg = (CircleImageView) findViewById(R.id.profile_img);
            choose_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog customdialog = new Dialog(SignUpActivity.this);
                    customdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    customdialog.setContentView(R.layout.camera_options);
                    customdialog.getWindow().setBackgroundDrawableResource(R.drawable.squre_corner_shape);

                    rl_capture = (RelativeLayout) customdialog.findViewById(R.id.rl_capture);
                    rl_gallery = (RelativeLayout) customdialog.findViewById(R.id.rl_gallery);

                    rl_capture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            capture();
                            customdialog.dismiss();
                        }
                    });
                    rl_gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            browse();
                            customdialog.dismiss();
                        }
                    });

                    customdialog.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void browse() {
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void capture() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE) {

            if (resultCode == RESULT_OK) {
                onCaptureImageResult(data);
            }

        } else if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                imagePath = null;
                Uri picUri = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(picUri, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                imagePath = c.getString(columnIndex);

                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), picUri);
                    profileString = getEncodedImage(bitmapImage);
//                    profile_img.setImageURI(picUri);
                    profileImg.setImageBitmap(bitmapImage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
//                profile_img.setImageURI(Uri.parse(imagePath));
                Log.d("Selected Image path: ", imagePath);

                c.close();
            }
        }
    }
    private void copyFileStream(File dest, Uri uri, Context context)
        throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    private Uri getOutputImageFileUri(int mediaTypeImage) {
        return Uri.fromFile(getOutputImageFile(mediaTypeImage));
    }
    private File getOutputImageFile(int mediaTypeImage) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraImage");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraVideo", "Failed to create directory MyCameraImage.");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;

        if (mediaTypeImage == MEDIA_TYPE_IMAGE) {

            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        } else {
            return null;
        }

        return mediaFile;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        Log.d("MIME_TYPE_EXT", extension);
        if (extension != null && extension != "") {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            //  Log.d("MIME_TYPE", type);
        } else {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            type = fileNameMap.getContentTypeFor(url);
        }
        return type;
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(getApplicationContext().getFilesDir().getPath(),
            System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            PickedImgPath = destination.getAbsolutePath();
            Log.e("Camera Path", destination.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bmp = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        profileString = getEncodedImage(bmp);
        profileImg.setImageBitmap(bmp);
    }
}


