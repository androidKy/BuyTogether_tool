package com.utils.common.rsa;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * RSA算法，实现数据的加密解密。
 */
public class RSAUtil {
    private static Cipher cipher;

    static {
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        //keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        keyBytes = Base64.decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }


    /**
     * 使用私钥对密文进行解密
     *
     * @param privateKey 私钥
     * @param enStr      密文
     * @return
     */
    public static String decrypt(String privateKey, String enStr) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
            byte[] deBytes = cipher.doFinal(Base64.decode(enStr));
            return new String(deBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void main(String[] args) {

//    	generateKeyPair("D:/RSA");

        String publicKey;
        String privateKey;

        publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAviSuCu4Yg/WAyjp06qiaE/ioI2M/ACT9UTUVxWtM7IZlXMQZPjLn0H1x0zmJ/VLIhnBliyb06QLvtrrBFRt4jnOJR5LjoTg/g8XYdVXN6a+XFjqFvOUPgzZ7OdywOoXxiO+M7WrvT0XgqyBqCnDADpY1eucDqfIDYYOBHKbtMkh0N4ZVBcfULb1Sm+Q7ed+jUa8eXPQPhMrWvhQkIeZJh+hCIrNjXUxyfZPh1tSvqoJYArbyHZs8LnbUtjIQCx9OlR9+xJTx3L9h89I4D+hqA4CZqxUzfibsu5XgYKnoSri2OCR2FefSfYlCd8Fysp0wET/r1L141qnhoMQtrUs8jwIDAQAB";
        // privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC+JK4K7hiD9YDKOnTqqJoT+KgjYz8AJP1RNRXFa0zshmVcxBk+MufQfXHTOYn9UsiGcGWLJvTpAu+2usEVG3iOc4lHkuOhOD+Dxdh1Vc3pr5cWOoW85Q+DNns53LA6hfGI74ztau9PReCrIGoKcMAOljV65wOp8gNhg4Ecpu0ySHQ3hlUFx9QtvVKb5Dt536NRrx5c9A+Eyta+FCQh5kmH6EIis2NdTHJ9k+HW1K+qglgCtvIdmzwudtS2MhALH06VH37ElPHcv2Hz0jgP6GoDgJmrFTN+Juy7leBgqehKuLY4JHYV59J9iUJ3wXKynTARP+vUvXjWqeGgxC2tSzyPAgMBAAECggEAMhFkhtpFOFIoFJgp+zRkRgf+9jqG91nGHmEVF4P2oH2PKUs1vmwXII43r8AB9uOai9QC2Q5sBQNR7dLlTtKJ/zCrIF6sc+JkzyUEp3jtnLAw35iPaLsER6/L6OOUwARPIpi5ijbTRxOGYmlJovAnkm+5K2CzVUe13jKLh+joool/ReZk0Rsr4tVLSLmvzDA/sRwYun0x0+jl5EZSQfwsVyN9bD5rY/In/EuvH9yj5R4lPe+mimF4Os6IgTsP5LzqDTAiFx5NNioFRJ2SkcTmM0CZQeMIBuvvF2HCtJlDEfCytD7wYup3GBvar2ccOe9T3YhJdsj5bfAJHVJtamxQwQKBgQDnYReMMzqAh2HOFL8QymzOjImsrOz6NCZatq38TU1hSe9PK+C0sFGhkd788y4AuURS1Btu4i7F+hOYcj1z3L+NSPGE3yLHVjakMrrNbA9rwG/t7oU0cG7d0WWM9bcTQiCSNcUyt69BGH3dZdqee1tITzqghE7+gh9RYiVcI6/8LwKBgQDSYEuWFLMUsR/s7unSHCucuEXjwbYrvknv8Y81sjvrWktNXrJoYlbGy/7HYA6lxzchtSxhPuUSjopwQ5scgMhqf8Gxz7jsDN9ak2dErF7cWRFYfh6aKhkbEw9oG01jIX15MK0TbMafoJslDhPQF1cP9i0+ZGg+gPbASdeUVRTNoQKBgQCOjwDOLgYeiMtXCOtL8hymCmsNDCKaaiUzgRijuhEyHzamJhe13Gj/TnwAh+hRI9UX333jjNJawqDuLXz1dQ5Eg6vjPQQVo2XZNzRnOuwpbJDKHUrPK3Lzkn+qIP6ii/y7eQu+GvSM/AUYsxfGy6RLYh1yJvLw1sVrBDiWk5prmwKBgFvgrmI3XBa3XKgPl5KptupVGEDmAveLvaLLLq5WzxB0eNqrduNbv2ZHBVhxvTPtk0hnZaB65XR7SD7LZ9zE6cKJVUCg5bRB0vIt2jYFydAWHhs1yYuuwxQt+NaQxfV7VN8uwQfww7ZHYDqIsWJ6Lw3Lh+rt0xEpJZrJJRulJNbBAoGBAK1OEnfBpSB99N8gdhp+ZGLsDfwFCQ2Cd4Jpsd4hxdwbXevNuA1OiE20sHPuKqEqfOKocgTMobCwbSfnymatRydVoeUumkEc4Ja+XDgH+P1eXQLdIuRCwh0AXl+vkuOCBDMw367Zp/j6vwPlNKh9ZmOBPwhV0Syv2Z8uGkTZ6g+f";
        privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKRYDkdpRUUV9IAq\n" +
                "sQ3Nhlti/SU7Kmv6mOyv4mNUGw8/pEP8QyL/c+LcOIrxxtr8FWMBB1IyElMEjhhZ\n" +
                "Q4+wfpLgmVWAH0RlbRzdeEVjf2F18CJIbAtyFpo+vZbdDsNn39AVHXUIKM3DobBu\n" +
                "XMrGH3LQJkIKR1daOpqs3Bub1Z/XAgMBAAECgYALj4mFmtFSyvjARqszd4iNh4Ob\n" +
                "lF4SFXafeRSZpyd2JaRxBzRT0xjRqxXWfwEy6LZlGK6X/h9i3qL+RyflBOXfuJ46\n" +
                "e83QcwXyCjz6eTRN8zq5fGL2RSK9qsHlPcKvu/JpsPUSSKUdxYfEdSQEW6Y72svE\n" +
                "ShVISVjJzFcabr15MQJBAMXa8aNdda7qCiU9N0teGrhGmoWp0j77A+r0y4jbn6CW\n" +
                "eCXCBhRfTXWhfLaHuP16uOD0Dg6FmIyLW4wDceTWl/ECQQDUo/1MMocGMX8yCvIP\n" +
                "bJfsTBltRiS47IJa9BRcJ1vKf8EywVziSqh2fSTZvmLBRX46sz1Pu1uAGLUcqyjx\n" +
                "BTxHAkBJi6YwrKf3GeYli4jHSjaycwQzVTni0VnWd6JEwCapAeWtW175KafYZuu9\n" +
                "yduY1YptjCrBeSWIcLDZ7dnjT2VxAkEAzbd1N0FuxbwfDR/vvKJXfCDHPKNupYUS\n" +
                "O8IE7H6bXBYqp9rP7JFD15YMj6eTzN1ZWJrNRxTAWfYNSZYBPuPMwwJBAJJvJy8t\n" +
                "DUtaZartVKZ+inXbdVdLK1+dWdtOpt3zB0anMLGYmZR0E5w4S+1rB8Aqb5WXxmAq\n" +
                "9hbBZKQQwOUrrPM=";


        System.err.println("公钥加密——私钥解密");
        String source = "psw123456";
        System.out.println("\r加密前文字：\r\n" + source);
        //String aData = RSAUtil.encrypt(publicKey, source);
        String aData = "Liz1oc6akzAFpUejxHRhUv9PAocyjXKU+ZUPfvOHnZ/Sgrs/68tn9ZArjtYEhwNTCs3nXHeu2N6zf4r++ooevkJ32E9ap/48j+ujwCkNcVK0Jem6WaigpzwurZa24Oe1kukwlQeeH0FCP0a2uKsxF3p0hu9UbSvlxXRh4UHt2So=";
        System.out.println("加密后文字：\r\n" + aData);
        String dData = RSAUtil.decrypt(privateKey, aData);
        System.out.println("解密后文字: \r\n" + dData);
    }
}

