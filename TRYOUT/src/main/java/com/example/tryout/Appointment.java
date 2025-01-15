package com.example.tryout;

public class Appointment {
    private String data_programare;
    private String ora_programare;
    private String serviciu;
    private String status;

    public Appointment(String date, String time, String service, String status) {
        this.data_programare = date;
        this.ora_programare = time;
        this.serviciu = service;
        this.status = status;
    }

    public String getDate() {
        return data_programare;
    }

    public String getTime() {
        return ora_programare;
    }

    public String getService() {
        return serviciu;
    }

    public String getStatus() {
        return status;
    }
}
