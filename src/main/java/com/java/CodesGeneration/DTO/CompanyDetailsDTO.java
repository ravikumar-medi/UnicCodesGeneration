package com.java.CodesGeneration.DTO;

import lombok.Data;

@Data
public class CompanyDetailsDTO {
    private String companyId;
    private String companyName;
    private String plantId;
    private String plantName;
    private String lineId;
    private String lineName;
    private int    year;
    private String uidCodeType;
    private int    noOfCodes;
    private int    noOfLots;
}