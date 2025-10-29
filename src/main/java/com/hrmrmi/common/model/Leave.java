package com.hrmrmi.common.model;

import java.util.Date;

public class Leave {
    private final Date startDate;
    private final Date endDate;
    private final String status;

    public Leave(Date startDate, Date endDate, String status){
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }


}
