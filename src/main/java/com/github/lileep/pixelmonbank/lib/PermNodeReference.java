package com.github.lileep.pixelmonbank.lib;

public class PermNodeReference {

    public static final String USER_NODE = "user";
    public static final String ADMIN_NODE = "admin";

    public static final String BYPASS_NODE = Reference.MOD_ID + "." + ADMIN_NODE + ".bypass";
    public static final String SEND_NODE = Reference.MOD_ID + "." + USER_NODE + ".send";
    public static final String GET_NODE = Reference.MOD_ID + "." + USER_NODE + ".get";
    public static final String SEE_NODE = Reference.MOD_ID + "." + USER_NODE + ".see";

}
