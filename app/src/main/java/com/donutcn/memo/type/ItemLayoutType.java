package com.donutcn.memo.type;

import android.support.annotation.IntDef;

/**
 * com.donutcn.memo.type
 * Created by 73958 on 2017/7/9.
 */

@IntDef({ItemLayoutType.TYPE_TAG, ItemLayoutType.AVATAR_IMG})
public @interface ItemLayoutType {

    int TYPE_TAG = 0;   // publish content type tag in item layout.

    int AVATAR_IMG = 1; // user icon in item layout.
}
