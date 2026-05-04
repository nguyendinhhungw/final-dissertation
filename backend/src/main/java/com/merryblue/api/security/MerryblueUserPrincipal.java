package com.merryblue.api.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerryblueUserPrincipal implements Principal {
    private String id; // user_id from Supabase Auth (UUID string)
    private String email;

    @Override
    public String getName() {
        return email != null ? email : id;
    }
}
