package kr.ac.knu.cse.presentation.dto;

import java.util.List;

public record ArchitectureDocResponse(
        String updatedAt,
        List<Section> sections
) {
    public record Section(
            String id,
            String title,
            String summary,
            List<Block> blocks
    ) {}

    public record Block(
            String type,
            String heading,
            List<String> bullets,
            List<Row> rows,
            List<String> headers
    ) {}

    public record Row(List<String> cells) {}
}
