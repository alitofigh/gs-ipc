package org.gsstation.novin.core.module;

import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;
import org.jpos.q2.QBeanSupport;
import org.jpos.util.NameRegistrar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.gsstation.novin.util.security.SecurityUtil.decryptCredentialAllParamsPredefined;

/**
 * Created by A_Tofigh at 8/14/2024
 */

public class KeyManagement extends QBeanSupport {
    private static final String THIS_CLASS_NAME = "key-management";

    private static Map<String, byte[]> macKeys = new HashMap<>();

    @Override
    protected void startService() throws Exception {
        super.startService();
        loadKeys();
        NameRegistrar.register(getName(), this);
    }

    @Override
    protected void stopService() throws Exception {
        NameRegistrar.unregister(getName());
        super.stopService();
    }

    @Override
    protected void initService() throws Exception {
        super.initService();
    }

    @Override
    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }

    public byte[] getKey(String key) {
        return macKeys.get(key);
    }

    public void setKey(String key, byte[] value) {
        macKeys.put(key, value);
    }

    private void loadKeys() throws Exception {
        Properties keysProperties = new Properties();
        try {
            File terminalKeysFile = new File("terminal-key.properties");
            InputStream inputStream = new FileInputStream(terminalKeysFile);
            keysProperties.load(inputStream);
            inputStream.close();
            keysProperties.stringPropertyNames().forEach(propertyKey -> {
                String key;
                try {
                    key = decryptCredentialAllParamsPredefined(keysProperties.getProperty(propertyKey));
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
                macKeys.put(propertyKey, ISOUtil.hex2byte(key));
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
