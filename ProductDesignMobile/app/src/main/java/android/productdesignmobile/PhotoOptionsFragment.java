package android.productdesignmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoOptionsFragment extends Fragment implements View.OnClickListener {

    private Button buttonAddPictureFile;
    private ImageView imageViewProfilePicture;
    private static final int GALLERY = 999;
    private ImageView mSelectedImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_options, parent, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imageViewProfilePicture = (ImageView) getActivity().findViewById(R.id.imageViewProfilePicture);
        buttonAddPictureFile = (Button) getActivity().findViewById(R.id.buttonAddPictureFile);
        buttonAddPictureFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.buttonAddPictureFile:
                    choosePhotoFromGallery();
            }
        }


    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY  && resultCode == Activity.RESULT_OK) {
            MainActivity activity = (MainActivity)getActivity();
            Bitmap bitmap = getBitmapFromCameraData(data, activity);
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
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }
}


