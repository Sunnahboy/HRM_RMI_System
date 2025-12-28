package com.hrmrmi.server;

import com.hrmrmi.common.util.Config;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.io.File;
import java.rmi.registry.Registry;

public class HRMServer {
    public static void main(String[] args) throws RemoteException {
        try {
            String currentDir = System.getProperty("user.dir");
            String keystorePath = currentDir + File.separator +
                    "src" + File.separator + "main" + File.separator +
                    "resources" + File.separator + "security" + File.separator +
                    "server.keystore";

            File keystore = new File(keystorePath);

            if(!keystore.exists()) {
                System.err.println("CRITICAL: Server keystore not found at path: " + keystorePath);
                System.exit(1);
            }
            System.out.println("Keystore found: " + keystorePath);

            System.setProperty("javax.net.ssl.keyStore", keystorePath);
            System.setProperty("javax.net.ssl.keyStorePassword", "changeit");

            System.setProperty("java.rmi.server.hostname", Config.RMI_HOST);

            try {
                LocateRegistry.createRegistry(Config.RMI_PORT);
                System.out.println("RMI Registry started on port " + Config.RMI_PORT);
            } catch (Exception e) {
                System.out.println("Registry already running");
            }

            HRMServiceImpl service = new HRMServiceImpl();

            String url = "rmi://" + Config.RMI_HOST + ":" + Config.RMI_PORT + "/" + Config.RMI_NAME;
            Naming.rebind(url, service);

            System.out.println("HRM RMI Server bound as " + Config.RMI_NAME + " on port " + Config.RMI_PORT +" at IP: " + Config.RMI_HOST);
            System.out.println("SSL Enabled");

            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }

        }catch(Exception e){
            System.err.println("Server crashed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
