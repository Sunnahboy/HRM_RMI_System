package com.hrmrmi.common.util;

public class Config {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/HRM_Service";
//    public static final String DB_USER = "postgres";
//    public static final String DB_PASSWORD = "Raikiri1000";
    public static final String DB_USER = System.getenv("MY_DB_USER") != null ? System.getenv("MY_DB_USER") : "YOUR_DB_USERNAME";
    public static final String DB_PASSWORD = System.getenv("MY_DB_PASS") != null ? System.getenv("MY_DB_PASS") : "YOUR_DB_PASSWORD";
    public static final int RMI_PORT = 54321;
    public static final String  RMI_NAME = "HRMService";

    //USE PORT FORWARDING! MORE MARKS
    public static final String RMI_HOST = System.getenv("HRM_SERVER_IP") != null ? System.getenv("HRM_SERVER_IP") : "localhost";
    public static final String DB_DRIVER = "org.postgresql.Driver";

    //Not required right now,but will prevent duplication later.
    public static final String KEYSTORE_PATH = "src/main/resources/security/server.keystore";
    public static final String TRUSTSTORE_PATH = "src/main/resources/security/client.truststore";


}
