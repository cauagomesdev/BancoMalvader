package com.bancomalvader.util;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordHasher {

   
    public static String hashPasswordMD5(String password) {
        
        return DigestUtils.md5Hex(password).toUpperCase();
    }

       public static boolean checkPassword(String plainPassword, String hashedPassword) {
        
        return hashPasswordMD5(plainPassword).equals(hashedPassword);
    }

}
