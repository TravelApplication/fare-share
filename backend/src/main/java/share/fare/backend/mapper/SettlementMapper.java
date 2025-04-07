package share.fare.backend.mapper;

import share.fare.backend.dto.request.SettlementRequest;
import share.fare.backend.dto.response.SettlementResponse;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.Settlement;
import share.fare.backend.entity.User;

import java.time.LocalDateTime;

public class SettlementMapper {
    public static Settlement toEntity(SettlementRequest settlementRequest, Group group, User paidBy, User paidTo) {
        if (settlementRequest == null) {
            return null;
        }

        return Settlement.builder()
                .group(group)
                .paidByUser(paidBy)
                .createdAt(LocalDateTime.now())
                .amount(settlementRequest.getAmount())
                .paidToUser(paidTo)
                .build();
    }

    public static SettlementResponse toResponse(Settlement settlement) {
        if (settlement == null) {
            return null;
        }

        return SettlementResponse.builder()
                .id(settlement.getId())
                .groupId(settlement.getGroup() != null ? settlement.getGroup().getId() : null)
                .paidByUserId(settlement.getPaidByUser() != null ? settlement.getPaidByUser().getId() : null)
                .paidToUserId(settlement.getPaidToUser() != null ? settlement.getPaidToUser().getId() : null)
                .amount(settlement.getAmount())
                .paymentDate(settlement.getCreatedAt())
                .build();
    }
}
