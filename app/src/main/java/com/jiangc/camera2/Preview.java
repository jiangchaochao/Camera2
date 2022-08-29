package com.jiangc.camera2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.TextureView;


/**
 * Created by Administrator on 2017/11/2.
 *
 */

@SuppressLint("ViewConstructor")
public class Preview extends TextureView  {
    private final String TAG = "Preview";
    public Preview(Context context, SurfaceTextureListener listener) {
        super(context);
        setSurfaceTextureListener(listener);
    }

}
