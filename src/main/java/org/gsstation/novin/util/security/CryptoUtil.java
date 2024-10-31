package org.gsstation.novin.util.security;

import org.gsstation.novin.core.logging.GsLogger;
import org.gsstation.novin.util.datetime.DateHelper;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.ISO87BPackager;
import org.jpos.iso.packager.ISO93BPackager;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class CryptoUtil {

    public static byte[] DESencryption(byte[] msg, byte[] key)
            throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {

        KeySpec ks = new DESKeySpec(key);

        SecretKeyFactory kf;
        kf = SecretKeyFactory.getInstance("DES");

        SecretKey ky;
        ky = kf.generateSecret(ks);

        Cipher cf = Cipher.getInstance("DES/ECB/NoPadding");
        cf.init(Cipher.ENCRYPT_MODE, ky);
        byte[] theCph = cf.doFinal(msg);
        return theCph;
    }

    public static byte[] DESdecryption(byte[] msg, byte[] key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeySpecException {

        SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
        Cipher cf = Cipher.getInstance("DES/ECB/NoPadding");
        DESKeySpec ks = new DESKeySpec(key);
        SecretKey ky = kf.generateSecret(ks);
        cf.init(Cipher.DECRYPT_MODE, ky);
        byte[] theCph = cf.doFinal(msg);
        return theCph;
    }

    public static String TripleDesBufferEncryption(String data, String key1,
                                                   String key2) throws Exception {
        String key = key1 + key2 + key1;
        byte[] bKey = ISOUtil.hex2byte(key);
        byte[] bdata = data.getBytes();
        return ISOUtil.hexString(TripleDESEncrypt(bKey, bdata));
    }

    public static String TripleDesBufferDecryption(String data, String key1,
                                                   String key2) throws Exception {
        String key = ISOUtil.padright(key1, 16, '0')
                + ISOUtil.padright(key2, 16, '0')
                + ISOUtil.padright(key1, 16, '0');
        byte[] bKey = ISOUtil.hex2byte(key);
        byte[] bdata = ISOUtil.hex2byte(data);
        return ISOUtil.hexString(TripleDESDecrypt(bKey, bdata));
    }

    private static byte[] TripleDESEncrypt(byte[] key, byte[] msg)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidKeySpecException,
            IllegalBlockSizeException, BadPaddingException {

        SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");
        Cipher cf = Cipher.getInstance("DESede/ECB/NoPadding");
        DESedeKeySpec ks = new DESedeKeySpec(key);
        SecretKey ky = kf.generateSecret(ks);
        cf.init(Cipher.ENCRYPT_MODE, ky);
        byte[] theCph = cf.doFinal(msg);
        return theCph;
    }

    public static byte[] TripleDESDecrypt(byte[] key, byte[] msg)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidKeySpecException,
            IllegalBlockSizeException, BadPaddingException {
        SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");
        Cipher cf = Cipher.getInstance("DESede/ECB/NoPadding");
        DESedeKeySpec ks = new DESedeKeySpec(key);
        SecretKey ky = kf.generateSecret(ks);
        cf.init(Cipher.DECRYPT_MODE, ky);
        byte[] theCph = cf.doFinal(msg);
        return theCph;
    }

    public static String Hex2Ascii(String original) {
        char[] str = new char[original.length() / 2];
        int j = 0;
        for (int i = 0; i < original.length(); i += 2) {
            char[] data = new char[2];
            data[0] = original.charAt(i);
            data[1] = original.charAt(i + 1);
            String temp = String.valueOf(data);
            int val = Integer.valueOf(temp, 16);
            str[j++] = (char) (val);
        }
        return ISOUtil.unPadLeft(String.valueOf(str), '0');
    }

    public static byte[] calculateMACDES(byte[] key, byte[] data,
                                         int decrementLenForMAC) throws InvalidKeyException,
            NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {
        byte[] block = new byte[8];
        int paddingLen;
        byte[] paddedData;
        byte[] res = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };

        paddingLen = 8 - (data.length % 8 == 0 ? 8 : data.length % 8);
        paddedData = new byte[data.length - decrementLenForMAC + paddingLen];
        System.arraycopy(data, 0, paddedData, 0, data.length
                - decrementLenForMAC);
        System.arraycopy(res, 0, paddedData, data.length - decrementLenForMAC,
                paddingLen);

        for (int i = 0; i < paddedData.length; i += 8) {
            System.arraycopy(paddedData, i, block, 0, 8);
            res = ISOUtil.xor(res, block);
            res = DESencryption(res, key);
        }
        return res;
    }

    public static boolean validateMessageMAC(ISOMsg m, String MacKey) throws Exception {
        byte[] key1 = ISOUtil.hex2byte(MacKey);
        int macLen = 16;
        int macfield = 64;
        if (m.hasField(128))
            macfield = 128;

        if (m.getPackager() instanceof ISO87BPackager
                || m.getPackager() instanceof ISO93BPackager)
            macLen = 8;

        //byte[] mac = calculateMACDES(key1, m.getOrginalData(), macLen);
        byte[] mac = calculateMACDES(key1, m.pack(), macLen);

        if (!ISOUtil.hexString(mac).equals(ISOUtil.hexString(m.getBytes(macfield)))) {
            GsLogger.log(
                    "Invalid MAC Calculated MAC : " + ISOUtil.hexString(mac)
                            + " Message MAC : "
                            + ISOUtil.hexString(m.getBytes(macfield)), m);
            //CmLogger.log(ISOUtil.hexdump(m.getOrginalData()));
            //CmLogger.log(ISOUtil.dumpString(m.getOrginalData()));
            return false;
        }
        return true;
    }

    public static void changePinEncryption(ISOMsg msg, String fromPinKey,
                                           String toPinKey) throws Exception {

        byte[] pin = msg.getBytes(52);
        String pan = null;
        if (msg.hasField(2))
            pan = msg.getString(2);
        else
            pan = msg.getString(35).substring(0, 16);
        String rawPin = decryptPINBlock(pin, pan, fromPinKey);
        // CmLogger.logMessage("RawPin is : " + rawPin);
        pin = encryptPINBlock(rawPin, pan, toPinKey);
        msg.set(52, pin);
    }

    public static String decryptUssdContent(byte[] msg) throws Exception {
        byte[] key = "11223344".getBytes();
        String result = new String(DESdecryption(msg, key));
        return result;

    }

    public static void changePinEncryptionFaraz(ISOMsg msg, String fromPinKey,
                                                String toPinKey) throws Exception {
        byte[] pin = msg.getBytes(52);
        String pan = null;
        if (msg.hasField(2))
            pan = msg.getString(2);
        else
            pan = msg.getString(35).substring(0, 16);

        String rawPin;
        pin = CryptoUtil.DESdecryption(pin, ISOUtil.hex2byte(fromPinKey));
        rawPin = ISOUtil.hexString(pin);
        int len = ISOUtil.parseInt(rawPin.substring(0, 2), 16);
        //if (!pan.substring(0, 14 - len).equals(rawPin.substring(2, 16 - len))) {
        //  System.out.println(DateHelper.currentPersianDate());
        //}

        rawPin = rawPin.substring(16 - len, 16);
        // CmLogger.logMessage("RawPin is : " + rawPin);
        pin = CryptoUtil.encryptPINBlock(rawPin, pan, toPinKey);
        msg.set(52, pin);
    }

    public static void changePinEncryptionFaraz(ISOMsg msg, byte[] pin, String fromPinKey,
                                                String toPinKey) throws Exception {

        String pan = null;
        if (msg.hasField(2))
            pan = msg.getString(2);
        else
            pan = msg.getString(35).substring(0, 16);

        String rawPin;
        pin = CryptoUtil.DESdecryption(pin, ISOUtil.hex2byte(fromPinKey));
        rawPin = ISOUtil.hexString(pin);
        int len = ISOUtil.parseInt(rawPin.substring(0, 2), 16);
        if (!pan.substring(0, 14 - len).equals(rawPin.substring(2, 16 - len))) {
            System.out.println(DateHelper.currentPersianDate());
        }

        rawPin = rawPin.substring(16 - len, 16);
        // CmLogger.logMessage("RawPin is : " + rawPin);
        pin = CryptoUtil.encryptPINBlock(rawPin, pan, toPinKey);
        msg.set(52, pin);
    }

    public static byte[] encryptPINBlock(String pin, String pan, String key)
            throws ISOException, InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        if (pan.length() < 16) {
            pan = ISOUtil.padright(pan, 16, '0');
        }
        String tempStr = ISOUtil.padleft(Integer.toHexString(pin.length()), 2,
                '0').concat(pin);
        pin = ISOUtil.padright(tempStr, 16, 'f');
        pan = "0000" + pan.substring(pan.length() - 13, pan.length() - 1);
        byte[] InPin = ISOUtil.hex2byte(pin);
        byte[] bPAN = ISOUtil.hex2byte(pan);
        InPin = ISOUtil.xor(InPin, bPAN);
        return DESencryption(InPin, ISOUtil.hex2byte(key));
    }

    public static String decryptPINBlock(byte[] pin, String pan, String key)
            throws Exception {
        byte[] outPin;
        byte[] key1, key2;
        outPin = DESdecryption(pin, ISOUtil.hex2byte(key));
        pan = "0000" + pan.substring(pan.length() - 13, pan.length() - 1);
        key1 = ISOUtil.hex2byte(pan);
        key2 = outPin;
        key1 = ISOUtil.xor(key1, key2);
        pan = ISOUtil.hexString(key1);
        int length = ISOUtil.parseInt(pan.substring(0, 2), 16);
        if (length > pan.length())
            length = 4;
        return pan.substring(2, 2 + length);
    }

    public static String DecryptUserPass(String userPass) throws Exception {
        String Key1 = ISOUtil.padright("85623146", 16, '0');
        String Key2 = ISOUtil.padright("87569423", 16, '0');
        userPass = CryptoUtil.TripleDesBufferDecryption(userPass, Key1, Key2);
        byte[] temp = ISOUtil.hex2byte(userPass);
        int i = 0;
        for (i = temp.length - 1; temp[i] == 0; i--)
            ;
        if (i != temp.length) {
            byte[] newPassword = new byte[i + 1];
            System.arraycopy(temp, 0, newPassword, 0, i + 1);
            userPass = new String(newPassword);
        } else
            userPass = new String(temp);
        //userPass = ISOUtil.unPadRight(userPass, '0');
        return userPass;
    }

    // @added by rsh
    public static String encryptUserPass(String userPass) throws Exception {
        int paddingLen = userPass.length() % 8 == 0 ? 0 : 8 - userPass.length() % 8;
        return CryptoUtil.TripleDesBufferEncryption(
                ISOUtil.padright(userPass, userPass.length() + paddingLen, (char) 0),
                ISOUtil.padright("85623146", 16, '0'), ISOUtil.padright("87569423", 16, '0'));
    }
}
