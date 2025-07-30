/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

public class ImagesGallery {

    private static boolean isBuildVersionQ() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q;
    }
    public static ArrayList<String> listOfImages(Context context){
        ArrayList<String> listOfAllImages = new ArrayList<>();
        if (isBuildVersionQ()){
            Cursor cursor;
            Uri uri;
            int column_index_data, column_index_folderName;
            String absolutePathOfImage;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
            String orderBy = MediaStore.Video.Media.DATE_TAKEN;
            cursor = context.getContentResolver().query(uri,projection, MediaStore.Images.Media.DATA + " like ? ",
                    new String[] {"%Pictures/Assessment/%"},orderBy+" DESC");
            /*
             cursor = context.getContentResolver().query(uri,projection, null,
                     null,orderBy+" DESC");
            */
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            //get folder name
            column_index_folderName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()){
                absolutePathOfImage = cursor.getString(column_index_data);
                listOfAllImages.add(absolutePathOfImage);
            }
        }
        else {
            try {
                File imageDir = new File(Environment.getExternalStorageDirectory(),"Pictures/Assessment/");
                File[] images = imageDir.listFiles();
                for (File image:
                     images) {
                    listOfAllImages.add(image.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       
        return listOfAllImages;
    }
}
