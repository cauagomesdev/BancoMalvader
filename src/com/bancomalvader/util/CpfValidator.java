package com.bancomalvader.util;

public class CpfValidator {

        public static boolean isValidFormat(String cpf) {
        
        cpf = cpf.replaceAll("[^0-9]", "");
        
        return cpf.length() == 11 && cpf.matches("\\d+");
    }

}
