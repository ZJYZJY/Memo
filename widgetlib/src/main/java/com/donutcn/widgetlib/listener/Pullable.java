package com.donutcn.widgetlib.listener;

public interface Pullable {
    /**
     * Determine if you can pull down，If you do not need the drop-down function just return false
     *
     * @return true if can pull up, else return false.
     */
    boolean canPullDown();

    /**
     * Determine if you can pull up，If you do not need the pull-up function just return false
     *
     * @return true if can pull up, else return false.
     */
    boolean canPullUp();
}
