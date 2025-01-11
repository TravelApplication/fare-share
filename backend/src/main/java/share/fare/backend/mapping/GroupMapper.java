package share.fare.backend.mapping;

import share.fare.backend.dto.response.GroupResponseDto;
import share.fare.backend.entity.Group;

public class GroupMapper {
    public static GroupResponseDto toResponseDto(Group group) {
        return GroupResponseDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdById(group.getCreatedBy() != null ? group.getCreatedBy().getId() : null)
                .build();
    }
}