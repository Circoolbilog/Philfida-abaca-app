package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;

public class ImagesGallery {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static ArrayList<String> listOfImages(Context context){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folderName;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri,projection, null,
                null,orderBy+" DESC");
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        //get folder name
        column_index_folderName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()){
            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }
}
