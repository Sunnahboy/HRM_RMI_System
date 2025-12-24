//package com.hrmrmi.client;
//
//import com.hrmrmi.common.HRMService;
//import com.hrmrmi.common.util.Config;
//import com.hrmrmi.server.HRMServiceImpl;
//
//import java.rmi.Naming;
//
//public class EmployeeClient {
//    public static void main(String[] args) {
//        try{
//            HRMService service = (HRMService) Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/" + Config.RMI_NAME);
//            boolean ok = service.login("admin","admin");
//            System.out.println("Login result: " + ok);
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//}
