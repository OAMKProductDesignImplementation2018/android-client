package android.productdesignmobile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.productdesignmobile.LoginActivity.session;

public class PhotoOptionsFragment extends Fragment {

    // Imageview to show the thumbnail of your profile picture
    private ImageView selectedImageThumbnail;
    // Path to the picture
    private String selectedImagePathRealSize;
    private File selectedImagePathThumbnail;

    //private final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png; boundary");

    private static final int CAMERA_PERMISSION = 20;
    private static final int STORAGE_PERMISSION = 21;

    private static final int CAMERA_INTENT = 22;
    private static final int GALLERY_INTENT = 23;

    private Button buttonUpload;
    private TextView status;

    FaceDetector detector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_options, parent, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Load views
        selectedImageThumbnail = view.findViewById(R.id.imageViewProfilePicture);
        status = view.findViewById(R.id.photoTextViewStatus);

        // Select picture from gallery
        final Button buttonAddPictureFile = view.findViewById(R.id.buttonAddPictureFile);
        buttonAddPictureFile.setOnClickListener(v -> {
            // Request permissions or launch gallery intent
            if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            else
                choosePhotoFromGallery();
        });

        // Capture new picture with camera
        final Button buttonUseCamera = view.findViewById(R.id.buttonAddPhotoCamera);
        buttonUseCamera.setOnClickListener(v -> {
            // Check if device has a camera
            if (getActivity() != null && !getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                Toast.makeText(getContext(), getString(R.string.noCameraAvailable), Toast.LENGTH_LONG).show();
                return;
            }

            // Request permissions or launch camera intent
            if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            else
                launchCamera();
        });

        // Upload chosen picture
        buttonUpload = view.findViewById(R.id.buttonSavePicture);
        buttonUpload.setAlpha(.5f);
        buttonUpload.setEnabled(false);
        buttonUpload.setOnClickListener(v -> {
            buttonUpload.setEnabled(false);
            buttonUpload.setAlpha(.5f);
            buttonUpload.setText("Uploading picture...");
            Thread thread = new Thread(() -> {
                String user_id = Integer.toString(session.getUserID());
                OkHttpClient client = new OkHttpClient();
                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addPart(Headers.of("Content-Disposition", "form-data; name=\"image\""),
                                RequestBody.create(MEDIA_TYPE_PNG, selectedImagePathThumbnail))
                        .build();
                Request request = new Request.Builder()
                        .addHeader("user_id", user_id)
                        .header("user_id", user_id)
                        .url("https://facedatabasetest.azurewebsites.net/api/MobileImageHandler")
                        .post(requestBody)
                        .build();
                Log.d("PRODU_DEV", "headers: " + request.headers().toString());
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()){
                        Toast.makeText(getActivity(), "Picture upload failed.", Toast.LENGTH_SHORT).show();
                        throw new IOException("Unexpected code " + response);
                    }
                    else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                buttonUpload.setEnabled(true);
                                buttonUpload.setAlpha(1f);
                                buttonUpload.setText(R.string.pictureSaveButton);
                                Toast.makeText(getActivity(), "Picture uploaded successfully!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Log.d("PRODU_DEV", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        });

        detector = new FaceDetector.Builder(getContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    // Permissions are granted, launch camera
                    launchCamera();
                break;
            case STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    // Permissions are granted, launch gallery
                    choosePhotoFromGallery();
                break;
            default:
                break;
        }
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // todo: fix thumbnail size and orientation change in some pictures
        final int THUMB_SIZE = 512;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_INTENT: {
                    if (data.getData() != null && getActivity() != null) {
                        buttonUpload.setEnabled(true);
                        buttonUpload.setAlpha(1f);
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        // Get path to the chosen picture
                        Cursor cursor = getActivity().getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                        if (cursor != null)
                        {
                            cursor.moveToFirst();
                            selectedImagePathRealSize = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                            cursor.close();
                        }
                        buttonUpload.setAlpha(1f);
                        buttonUpload.setEnabled(false);
                    }
                } // no break here
                case CAMERA_INTENT: {
                    buttonUpload.setEnabled(true);
                    buttonUpload.setAlpha(1f);

                    // Create a thumbnail of the picture
                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePathRealSize), THUMB_SIZE, THUMB_SIZE);
                    //selectedImageThumbnail.setImageBitmap(bitmap);
                    
                    // Check picture rotation
                    try {
                        ExifInterface exif = new ExifInterface(selectedImagePathRealSize);
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int rotationInDegrees = exifToDegrees(rotation);
                        Log.d("rotation", "" + rotationInDegrees);
                        Bitmap rotatedBitmap = rotateBitmap(bitmap, rotationInDegrees);
                        createTempImage(bitmap);
                        selectedImageThumbnail.setImageBitmap(rotatedBitmap);

                        // Detect face
                        Frame frame = new Frame.Builder().setBitmap(rotatedBitmap).build();
                        SparseArray<Face> faces = detector.detect(frame);
                        Log.d("facedetect", "Faces detected: " + String.valueOf(faces.size()));
                        if (faces.size() < 1){
                            status.setText("No faces detected!");
                            buttonUpload.setEnabled(false);
                            buttonUpload.setAlpha(.5f);
                        } else if (faces.size() > 1) {
                            status.setText("Too many faces detected!");
                            buttonUpload.setEnabled(false);
                            buttonUpload.setAlpha(.5f);
                        } else if (faces.size() == 1) {
                            status.setText("Face detected!");
                            buttonUpload.setEnabled(true);
                            buttonUpload.setAlpha(1f);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } break;
                default:
                    break;
            }
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getActivity() != null && cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create temporary file from the image
            File photo = null;
            try { photo = createTempImageFile(); }
            catch (IOException e) { e.printStackTrace(); }

            // Add the file name and location into intent
            if (photo != null && getContext() != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), "android.productdesignmobile", photo);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Save the file path for later use
                selectedImagePathRealSize = photo.getAbsolutePath();
            }
            startActivityForResult(cameraIntent, CAMERA_INTENT);
        }
    }

    private File createTempImageFile() throws IOException {
        // Creates new temporary image file and stores it in pictures
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + "_";

        File storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    private void createTempImage(Bitmap bitmap){
        File filesDir = getContext().getFilesDir();
        selectedImagePathThumbnail = new File(filesDir, "temp_pic" + ".jpg");
        OutputStream os;
        try {
            os = new FileOutputStream(selectedImagePathThumbnail);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
}
