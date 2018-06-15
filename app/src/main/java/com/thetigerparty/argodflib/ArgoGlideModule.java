package com.thetigerparty.argodflib;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Created by louis on 06/11/2017.
 */

@GlideModule
public class ArgoGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);

        builder.setLogLevel(Log.DEBUG);
    }
}
