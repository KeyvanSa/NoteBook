package ebookline.notepad.Util;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptingData
{
    public String decrypt(String value) throws Exception
    {
        String coded;
        if(value.startsWith("code==")){
            coded = value.substring(6,value.length()).trim();
        }else{
            coded = value.trim();
        }

        String result = null;

        try {
            // Decoding base64
            byte[] bytesDecoded = Base64.decode(coded.getBytes(StandardCharsets.UTF_8),Base64.DEFAULT);

            SecretKeySpec key = new SecretKeySpec(keySalt.getBytes(), "DES");

            @SuppressLint("GetInstance")
            Cipher cipher = Cipher.getInstance("DES/ECB/ZeroBytePadding");

            // Initialize the cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Decrypt the text
            byte[] textDecrypted = cipher.doFinal(bytesDecoded);

            result = new String(textDecrypted);

        } catch (Exception e) {
            throw new Exception();
        }
        return result;
    }

    public String encrypt(String value) throws Exception
    {
        String crypted = "";

        try {

            byte[] cleartext = value.getBytes(StandardCharsets.UTF_8);

            SecretKeySpec key = new SecretKeySpec(keySalt.getBytes(), "DES");

            @SuppressLint("GetInstance")
            Cipher cipher = Cipher.getInstance("DES/ECB/ZeroBytePadding");

            // Initialize the cipher for decryption
            cipher.init(Cipher.ENCRYPT_MODE, key);

            crypted = Base64.encodeToString(cipher.doFinal(cleartext),Base64.DEFAULT);


        }catch (Exception e) {
            throw new Exception();
        }

        return crypted;
    }

    private final static String keySalt = "asfl33dgd63ggfklk5ulej634ufergir";
    public EncryptingData(){}
}
