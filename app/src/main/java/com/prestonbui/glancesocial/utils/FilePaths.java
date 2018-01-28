package com.prestonbui.glancesocial.utils;

import android.os.Environment;

/**
 * Created by phuongbui on 10/21/17.
 */

public class FilePaths {

    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";

}
