package util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * Created by philipp on 24.01.15.
 */
public class UtilPL {


    public static void setPic(ImageView mImageView, String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = 300;//mImageView.getWidth();
        int targetH = 300;//mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }
}
