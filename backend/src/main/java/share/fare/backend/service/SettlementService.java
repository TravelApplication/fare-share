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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final GroupBalanceRepository groupBalanceRepository;

    @Transactional
    public SettlementResponse createPayment(SettlementRequest request) {
        List<Long> userIds = Arrays.asList(request.getDebtorId(), request.getCreditorId());
        List<GroupBalance> balances = groupBalanceRepository.findWithUsersByGroupAndUserIds(request.getGroupId(), userIds);

        if (balances.size() != 2) {
            throw new GroupBalanceNotFoundException("One or both of the users do not belong to the group.");
        }

        GroupBalance debtorBalance = balances.stream()
                .filter(gb -> gb.getUser().getId().equals(request.getDebtorId()))
                .findFirst()
                .orElseThrow(() -> new GroupBalanceNotFoundException(request.getGroupId(), request.getDebtorId()));

        GroupBalance creditorBalance = balances.stream()
                .filter(gb -> gb.getUser().getId().equals(request.getCreditorId()))
                .findFirst()
                .orElseThrow(() -> new GroupBalanceNotFoundException(request.getGroupId(), request.getCreditorId()));


        if (debtorBalance.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            throw new InvalidPaymentException("Debtor doesn't owe any money in this group.");
        }

        if (creditorBalance.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Creditor doesn't have any money to receive in this group.");
        }

        BigDecimal maxPaymentAmount = debtorBalance.getBalance().abs().min(creditorBalance.getBalance().abs());

        if (request.getAmount().compareTo(maxPaymentAmount) > 0) {
            throw new InvalidPaymentException("Settlement amount exceeds the maximum possible amount.");
        }

        debtorBalance.setBalance(debtorBalance.getBalance().add(request.getAmount()));
        creditorBalance.setBalance(creditorBalance.getBalance().subtract(request.getAmount()));

        Settlement settlement = Settlement.builder()
                .group(debtorBalance.getGroup())
                .paidByUser(debtorBalance.getUser())
                .paidToUser(creditorBalance.getUser())
                .amount(request.getAmount())
                .build();

        settlementRepository.save(settlement);

        return SettlementMapper.toResponse(settlement);
    }

    @Transactional
    public SettlementResponse updateSettlement(Long settlementId, SettlementRequest request, User authenticatedUser) {
        Settlement existingSettlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new SettlementNotFoundException(settlementId));

        if (!authenticatedUser.getId().equals(existingSettlement.getPaidByUser().getId()) &&
                !authenticatedUser.getId().equals(existingSettlement.getPaidToUser().getId())) {
            throw new ActionIsNotAllowedException("You can only modify your own settlements");
        }

        reverseSettlementEffect(existingSettlement);

        List<Long> userIds = Arrays.asList(request.getDebtorId(), request.getCreditorId());
        List<GroupBalance> balances = groupBalanceRepository.findWithUsersByGroupAndUserIds(
                request.getGroupId(), userIds);

        if (balances.size() != 2) {
            throw new GroupBalanceNotFoundException("One or both of the users do not belong to the group.");
        }

        GroupBalance debtorBalance = getBalanceFromList(balances, request.getDebtorId(), request.getGroupId());
        GroupBalance creditorBalance = getBalanceFromList(balances, request.getCreditorId(), request.getGroupId());

        validateBalancesForSettlement(debtorBalance, creditorBalance, request.getAmount());

        debtorBalance.setBalance(debtorBalance.getBalance().add(request.getAmount()));
        creditorBalance.setBalance(creditorBalance.getBalance().subtract(request.getAmount()));

        existingSettlement.setGroup(debtorBalance.getGroup());
        existingSettlement.setPaidByUser(debtorBalance.getUser());
        existingSettlement.setPaidToUser(creditorBalance.getUser());
        existingSettlement.setAmount(request.getAmount());

        return SettlementMapper.toResponse(settlementRepository.save(existingSettlement));
    }

    @Transactional
    public void deleteSettlement(Long settlementId, User authenticatedUser) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new SettlementNotFoundException(settlementId));

        if (!authenticatedUser.getId().equals(settlement.getPaidByUser().getId()) &&
                !authenticatedUser.getId().equals(settlement.getPaidToUser().getId())) {
            throw new ActionIsNotAllowedException("You can only delete your own settlements");
        }

        reverseSettlementEffect(settlement);

        settlementRepository.delete(settlement);
    }

    public List<SettlementResponse> getGroupPayments(Long groupId) {
        return settlementRepository.findByGroup_Id(groupId).stream()
                .map(SettlementMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void reverseSettlementEffect(Settlement settlement) {
        GroupBalance debtorBalance = groupBalanceRepository.findByGroupAndUser(
                        settlement.getGroup(), settlement.getPaidByUser())
                .orElseThrow(() -> new GroupBalanceNotFoundException(
                        settlement.getGroup().getId(), settlement.getPaidByUser().getId()));

        GroupBalance creditorBalance = groupBalanceRepository.findByGroupAndUser(
                        settlement.getGroup(), settlement.getPaidToUser())
                .orElseThrow(() -> new GroupBalanceNotFoundException(
                        settlement.getGroup().getId(), settlement.getPaidToUser().getId()));

        debtorBalance.setBalance(debtorBalance.getBalance().subtract(settlement.getAmount()));
        creditorBalance.setBalance(creditorBalance.getBalance().add(settlement.getAmount()));
    }

    private GroupBalance getBalanceFromList(List<GroupBalance> balances, Long userId, Long groupId) {
        return balances.stream()
                .filter(gb -> gb.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new GroupBalanceNotFoundException(groupId, userId));
    }

    private void validateBalancesForSettlement(GroupBalance debtorBalance, GroupBalance creditorBalance, BigDecimal amount) {
        if (debtorBalance.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            throw new InvalidPaymentException("Debtor doesn't owe any money in this group.");
        }

        if (creditorBalance.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Creditor doesn't have any money to receive in this group.");
        }

        BigDecimal maxPaymentAmount = debtorBalance.getBalance().abs().min(creditorBalance.getBalance());
        if (amount.compareTo(maxPaymentAmount) > 0) {
            throw new InvalidPaymentException("Settlement amount exceeds the maximum possible amount.");
        }
    }
}
