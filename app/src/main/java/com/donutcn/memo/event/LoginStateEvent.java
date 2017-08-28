package com.donutcn.memo.event;

import com.donutcn.memo.entity.User;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/8/8.
 */

public class LoginStateEvent {

    public static final int LOGIN = 0;
    public static final int SYNC = 1;
    public static final int LOGOUT = 2;

    private int state;
    private User user;

    public LoginStateEvent(int state, User user){
        this.state = state;
        this.user = user;
    }

    public boolean isLogin() {
        return state == LOGIN;
    }

    public boolean isSync(){
        return state == SYNC;
    }

    public boolean isLogout(){
        return state == LOGOUT;
    }

    public User getUser() {
        return user;
    }
}
