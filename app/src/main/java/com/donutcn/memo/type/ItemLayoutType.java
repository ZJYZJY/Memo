package com.donutcn.memo.type;

import android.support.annotation.IntDef;

/**
 * com.donutcn.memo.type
 * Created by 73958 on 2017/7/9.
 */

@IntDef({ItemLayoutType.NO_IMG, ItemLayoutType.TYPE_IMG, ItemLayoutType.AVATAR_IMG})
public @interface ItemLayoutType {

    int NO_IMG = 0;     // no image in item layout.

    int TYPE_IMG = 1;   // publish content type icon in item layout.

    int AVATAR_IMG = 2; // user icon in item layout.
}
