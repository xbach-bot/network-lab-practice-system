package com.example.test.domain.request.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PrivateRoomDTO {

    @NotBlank(message = "Target user ID is required")
    private Long targetUserId;
}