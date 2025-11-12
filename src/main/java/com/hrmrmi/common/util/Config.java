package com.hrmrmi.common.util;

public class Config {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/hrm_db";
    public static final String DB_USER = "hrm_user";
    public static final String DB_PASSWORD = "123456";
    public static final int RMI_PORT = 54321;
    public static final String  RMI_NAME = "HRMService";
    public static final String  RMI_HOST = "localhost";
    public static final String DB_DRIVER = "org.postgresql.Driver";

    //Not required right now,but will prevent duplication later.
    public static final String KEYSTORE_PATH = "src/main/resources/security/server.keystore";
    public static final String TRUSTSTORE_PATH = "src/main/resources/security/client.truststore";


}
