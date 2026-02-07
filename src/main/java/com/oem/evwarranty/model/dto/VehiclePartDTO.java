package com.oem.evwarranty.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class VehiclePartDTO {
    private Long id;
    private String serialNumber;
    private String partName; // From Part entity
    private String partCode; // From Part entity
    private LocalDate warrantyEndDate;
    private String status;
}
