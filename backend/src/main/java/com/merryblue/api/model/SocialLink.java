package com.merryblue.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "social_links")
@Data
public class SocialLink {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String platform;
    private String url;
    private String icon;
}
