package com.electronics.util;

public class SecurityUtil {

    public static String getCurrentUserEmail() {
        return "john@example.com";
    }

    // public static Jwt getJwt() {
    // return (Jwt)
    // SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // }
    //
    // public static String getEmail() {
    // return getJwt().getClaim("email");
    // }
}
