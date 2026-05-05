package com.classroomscheduler.dto;

public record ReservaLoteConflictResponse(
        String data,
        String inicio,
        String fim,
        String motivo
) {
}
