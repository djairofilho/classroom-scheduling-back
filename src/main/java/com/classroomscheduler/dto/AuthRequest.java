package com.classroomscheduler.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {

    private String email;

    private String senha;

    public AuthRequest() {
    }

}
