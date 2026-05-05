package com.merryblue.api.dto;

import lombok.Data;
import java.util.Map;

@Data
public class StatisticsDTO {
    private Map<String, Long> summary;
}
