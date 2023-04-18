package tech.iamwith.usergateway.utils;

import org.springframework.context.annotation.Bean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EmailHashHelper {

    @Bean
    public static String hashEmail(String email) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(email.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return "https://www.gravatar.com/avatar/" + sb.toString() + "?s=32&d=identicon&r=PG";
    }
}
