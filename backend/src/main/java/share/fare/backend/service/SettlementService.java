package share.fare.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.request.SettlementRequest;
import share.fare.backend.dto.response.SettlementResponse;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupBalance;
import share.fare.backend.entity.Settlement;
import share.fare.backend.entity.User;
import share.fare.backend.exception.*;
import share.fare.backend.mapper.SettlementMapper;
import share.fare.backend.repository.GroupBalanceRepository;
import share.fare.backend.repository.SettlementRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final GroupBalanceRepository groupBalanceRepository;

    @Transactional
    public SettlementResponse createPayment(SettlementRequest request) {
        Map<Long, GroupBalance> balances = getBalancesForGroup(request.getGroupId());
        validateSettlementRequest(request, balances);

        GroupBalance debtorBalance = balances.get(request.getDebtorId());
        GroupBalance creditorBalance = balances.get(request.getCreditorId());

        groupBalanceRepository.updateBalance(
                request.getGroupId(), request.getDebtorId(), request.getAmount());
        groupBalanceRepository.updateBalance(
                request.getGroupId(), request.getCreditorId(), request.getAmount().negate());

        Settlement settlement = buildSettlement(debtorBalance, creditorBalance, request.getAmount());
        return SettlementMapper.toResponse(settlementRepository.save(settlement));
    }

    @Transactional
    public SettlementResponse updateSettlement(Long settlementId, SettlementRequest request, User authenticatedUser) {
        Settlement existingSettlement = getSettlementWithAuthCheck(settlementId, authenticatedUser);
        Map<Long, GroupBalance> balances = getBalancesForGroup(request.getGroupId());

        validateSettlementRequest(request, balances);
        reverseSettlementEffect(existingSettlement);

        GroupBalance newDebtorBalance = balances.get(request.getDebtorId());
        GroupBalance newCreditorBalance = balances.get(request.getCreditorId());

        groupBalanceRepository.updateBalance(
                request.getGroupId(), request.getDebtorId(), request.getAmount());
        groupBalanceRepository.updateBalance(
                request.getGroupId(), request.getCreditorId(), request.getAmount().negate());

        updateExistingSettlement(existingSettlement, newDebtorBalance, newCreditorBalance, request.getAmount());
        return SettlementMapper.toResponse(settlementRepository.save(existingSettlement));
    }

    @Transactional
    public void deleteSettlement(Long settlementId, User authenticatedUser) {
        Settlement settlement = getSettlementWithAuthCheck(settlementId, authenticatedUser);
        reverseSettlementEffect(settlement);
        settlementRepository.delete(settlement);
    }

    public List<SettlementResponse> getGroupPayments(Long groupId) {
        return settlementRepository.findByGroup_Id(groupId).stream()
                .map(SettlementMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Map<Long, GroupBalance> getBalancesForGroup(Long groupId) {
        List<GroupBalance> balances = groupBalanceRepository.findAllWithUsersByGroupId(groupId);
        return balances.stream()
                .collect(Collectors.toMap(
                        gb -> gb.getUser().getId(),
                        Function.identity()
                ));
    }

    private void validateSettlementRequest(SettlementRequest request, Map<Long, GroupBalance> balances) {
        GroupBalance debtorBalance = balances.get(request.getDebtorId());
        GroupBalance creditorBalance = balances.get(request.getCreditorId());

        if (debtorBalance == null || creditorBalance == null) {
            throw new GroupBalanceNotFoundException("One or both users not found in group");
        }

        if (debtorBalance.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            throw new InvalidPaymentException("Debtor doesn't owe money in this group");
        }

        if (creditorBalance.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Creditor isn't owed money in this group");
        }

        BigDecimal maxAmount = debtorBalance.getBalance().abs().min(creditorBalance.getBalance());
        if (request.getAmount().compareTo(maxAmount) > 0) {
            throw new InvalidPaymentException("Amount exceeds maximum possible");
        }
    }

    private void reverseSettlementEffect(Settlement settlement) {
        groupBalanceRepository.updateBalance(
                settlement.getGroup().getId(),
                settlement.getPaidByUser().getId(),
                settlement.getAmount().negate());

        groupBalanceRepository.updateBalance(
                settlement.getGroup().getId(),
                settlement.getPaidToUser().getId(),
                settlement.getAmount());
    }

    private Settlement getSettlementWithAuthCheck(Long settlementId, User user) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new SettlementNotFoundException(settlementId));

        if (!user.getId().equals(settlement.getPaidByUser().getId()) &&
                !user.getId().equals(settlement.getPaidToUser().getId())) {
            throw new ActionIsNotAllowedException("Not authorized to modify this settlement");
        }
        return settlement;
    }

    private Settlement buildSettlement(GroupBalance debtorBalance, GroupBalance creditorBalance, BigDecimal amount) {
        return Settlement.builder()
                .group(debtorBalance.getGroup())
                .paidByUser(debtorBalance.getUser())
                .paidToUser(creditorBalance.getUser())
                .amount(amount)
                .build();
    }

    private void updateExistingSettlement(Settlement settlement, GroupBalance debtorBalance,
                                          GroupBalance creditorBalance, BigDecimal amount) {
        settlement.setGroup(debtorBalance.getGroup());
        settlement.setPaidByUser(debtorBalance.getUser());
        settlement.setPaidToUser(creditorBalance.getUser());
        settlement.setAmount(amount);
    }
}
