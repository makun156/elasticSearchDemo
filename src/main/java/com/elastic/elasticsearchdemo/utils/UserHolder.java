package com.elastic.elasticsearchdemo.utils;

import com.elastic.elasticsearchdemo.bean.TUserDTO;

public class UserHolder {
    private static final ThreadLocal<TUserDTO> tl = new ThreadLocal<>();

    public static void saveUser(TUserDTO user) {
        tl.set(user);
    }

    public static TUserDTO getUser() {
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }
}