package com.example.floodmonitoring;

public class Area {
    private String area_name;
    private String description;
    private String historical_data_bank;

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHistorical_data_bank() {
        return historical_data_bank;
    }

    public void setHistorical_data_bank(String historical_data_bank) {
        this.historical_data_bank = historical_data_bank;
    }
    public Area() {
        // Default constructor required for Firebase
    }
    // Include getters and setters
}
