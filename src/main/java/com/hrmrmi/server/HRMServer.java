package com.hrmrmi.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import com.hrmrmi.common.util.Config;
import java.rmi.registry.Registry;

public class HRMServer {
    public static void main(String[] args) throws RemoteException {
        try {
            System.setProperty("java.rmi.server.hostname", Config.RMI_HOST);

            Registry registry = LocateRegistry.createRegistry(Config.RMI_PORT);

            HRMServiceImpl service = new HRMServiceImpl();

            String url = "rmi://" + Config.RMI_HOST + ":" + Config.RMI_PORT + "/" + Config.RMI_NAME;
            Naming.rebind(url, service);
        System.out.println("HRM RMI Server bound as " + Config.RMI_NAME + " on port " + Config.RMI_PORT +" at IP: " + Config.RMI_HOST);

        }catch(Exception e){
            System.out.println("RMI Registry already running on port " + Config.RMI_PORT);
        }
    }
}
