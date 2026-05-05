package com.merryblue.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "system_configs")
@Data
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "config_key", unique = true)
    private String key;
    @Column(name = "config_value")
    private String value;
}
