package com.donutcn.memo.event;

import com.donutcn.memo.entity.User;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/8/8.
 */

public class LoginStateEvent {

    private boolean state;
    private User user;

    public LoginStateEvent(boolean state, User user){
        this.state = state;
        this.user = user;
    }

    public boolean isLogin() {
        return state;
    }

    public User getUser() {
        return user;
    }
}
