package android.productdesignmobile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PhotoOptionsFragment extends Fragment {

    // Imageview to show the thumbnail of your profile picture
    private ImageView selectedImageThumbnail;
    // Path to the picture
    private String selectedImagePathRealSize;

    private static final int CAMERA_PERMISSION = 20;
    private static final int STORAGE_PERMISSION = 21;

    private static final int CAMERA_INTENT = 22;
    private static final int GALLERY_INTENT = 23;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_options, parent, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Load views
        selectedImageThumbnail = view.findViewById(R.id.imageViewProfilePicture);

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
        final int THUMB_SIZE = 256;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_INTENT: {
                    if (data.getData() != null && getActivity() != null) {
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };

                        // Get path to the chosen picture
                        Cursor cursor = getActivity().getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                        if (cursor != null)
                        {
                            cursor.moveToFirst();
                            selectedImagePathRealSize = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                            cursor.close();
                        }
                    }
                } // no break here
                case CAMERA_INTENT: {
                    // Create a thumbnail of the picture
                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePathRealSize), THUMB_SIZE, THUMB_SIZE);
                    selectedImageThumbnail.setImageBitmap(bitmap);
                } break;
                default:
                    break;
            }
        }
    }

    private void launchCamera()
    {
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
}
