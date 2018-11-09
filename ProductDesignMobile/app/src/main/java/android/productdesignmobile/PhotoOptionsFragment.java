package android.productdesignmobile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

public class PhotoOptionsFragment extends Fragment {

    private ImageView imageViewProfilePicture;
    private ImageView mSelectedImage;

    private static final int CAMERA_PERMISSION = 20;
    private static final int CAMERA_INTENT = 21;
    private static final int GALLERY_INTENT = 22;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_options, parent, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Load views
        imageViewProfilePicture = view.findViewById(R.id.imageViewProfilePicture);

        // Select picture from gallery
        final Button buttonAddPictureFile = view.findViewById(R.id.buttonAddPictureFile);
        buttonAddPictureFile.setOnClickListener(v -> choosePhotoFromGallery());

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
            default:
                break;
        }
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_INTENT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {
            MainActivity activity = (MainActivity)getActivity();
            Bitmap bitmap = getBitmapFromCameraData(data, Objects.requireNonNull(activity));
            imageViewProfilePicture.setImageBitmap(bitmap);
        }
    }

    private void setFullImageFromFilePath(String imagePath) {
        // Get the dimensions of the View
        int targetW = mSelectedImage.getWidth();
        int targetH = mSelectedImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        mSelectedImage.setImageBitmap(bitmap);
    }

    public static Bitmap getBitmapFromCameraData(Intent data, Context context){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(Objects.requireNonNull(selectedImage),filePathColumn, null, null, null);
        Objects.requireNonNull(cursor).moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }

    void launchCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        if (getActivity() != null && cameraIntent.resolveActivity(getActivity().getPackageManager()) != null)
            getActivity().startActivityForResult(cameraIntent, CAMERA_INTENT);
    }
}


