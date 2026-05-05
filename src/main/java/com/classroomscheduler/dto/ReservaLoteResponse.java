package com.classroomscheduler.dto;

import java.util.List;

public record ReservaLoteResponse(
        int quantidadeCriada,
        int quantidadeIgnorada,
        List<Long> idsCriados,
        List<ReservaLoteConflictResponse> conflitos
) {
}
