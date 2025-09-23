package org.pahappa.systems.kpiTracker.models.crypto;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;

import javax.persistence.AttributeConverter;

public class StringCryptoConverter implements AttributeConverter<String, String> {

    private static final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();



    /**
     * This Static initializer block configures the "...singleton..." encryptor instance.
     * First, this block is executed only once; when the StringCryptoConverter class is first loaded.
     * Secondly, it Initializes the StandardPBEStringEncryptor
     * Then sets a strong, modern encryption algorithm (PBEWithHMACSHA512AndAES_256)
     */

    static{
        /**
         * a master password: set as an environment variable for production security
         */
        String masterPassword = System.getProperty("jasypt.encryptor.password");
        if(masterPassword == null || masterPassword.trim().isEmpty()) {
            //for when the master password isn't set
            System.out.println("WARNING: jasypt.encryptor.password system property not sent");
            masterPassword = "password123"; //default insecure password
        }
        encryptor.setPassword(masterPassword);
        encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256"); //the algorithm for encryption
    }


    /**
     * Encrypting the attribute before saving it to the database.
     * @param attribute: The plain-text string from the form(entity).
     * @return : The encrypted string to be stored in the database.
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        //checking if the value is already encrypted... so that it isn't encrypted twice
        if (PropertyValueEncryptionUtils.isEncryptedValue(attribute)) {
            return attribute;
        }
        return encryptor.encrypt(attribute);
    }


    /**
     * Decrypting data from the database column into the entity attribute.
     * @param dbData: The encrypted string from the database.
     * @return : The decrypted; plain-text string for the entity
     */
    @Override
    public String convertToEntityAttribute(String dbData){
        if(dbData == null){
            return null;
        }
        //checking if the value is encrypted before attempting to decrypt
        if (PropertyValueEncryptionUtils.isEncryptedValue(dbData)) {
            return encryptor.decrypt(dbData);
        }
        return dbData;
    }

}

