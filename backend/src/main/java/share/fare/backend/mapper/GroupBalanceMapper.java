package share.fare.backend.mapper;

import share.fare.backend.dto.response.GroupBalanceResponse;
import share.fare.backend.entity.GroupBalance;

public class GroupBalanceMapper {

    public static GroupBalanceResponse toResponse(GroupBalance groupBalance) {
        return GroupBalanceResponse.builder()
                .groupId(groupBalance.getGroup().getId())
                .userId(groupBalance.getUser().getId())
                .balance(groupBalance.getBalance())
                .build();
    }

}
