package com.hrmrmi.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import com.hrmrmi.common.util.Config;

public class HRMServer {
    public static void main(String[] args) throws RemoteException {
        try {
            LocateRegistry.createRegistry(Config.RMI_PORT);
            HRMServiceImpl impl = new HRMServiceImpl();
            Naming.rebind("rmi://localhost:" + Config.RMI_PORT + "/" + Config.RMI_NAME, impl);
        System.out.println("HRM RMI Server bound as " + Config.RMI_NAME + " on port " + Config.RMI_PORT);

        }catch(Exception e){
            System.out.println("RMI Registry already running on port " + Config.RMI_PORT);
        }
    }
}
