package share.fare.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import share.fare.backend.dto.request.SettlementRequest;
import share.fare.backend.dto.response.SettlementResponse;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupBalance;
import share.fare.backend.entity.Settlement;
import share.fare.backend.entity.User;
import share.fare.backend.exception.ActionIsNotAllowedException;
import share.fare.backend.exception.GroupBalanceNotFoundException;
import share.fare.backend.exception.InvalidPaymentException;
import share.fare.backend.exception.SettlementNotFoundException;
import share.fare.backend.repository.GroupBalanceRepository;
import share.fare.backend.repository.SettlementRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {
    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private GroupBalanceRepository groupBalanceRepository;

    @InjectMocks
    private SettlementService settlementService;

    private final Long GROUP_ID = 1L;
    private final Long DEBTOR_ID = 2L;
    private final Long CREDITOR_ID = 3L;
    private final BigDecimal AMOUNT = new BigDecimal("50.00");
    private final User authenticatedUser = User.builder().id(DEBTOR_ID).build();

    private GroupBalance createGroupBalance(Long userId, BigDecimal balance) {
        return GroupBalance.builder()
                .group(Group.builder().id(GROUP_ID).build())
                .user(User.builder().id(userId).build())
                .balance(balance)
                .build();
    }

    private Settlement createSettlement(Long id, Long paidById, Long paidToId, BigDecimal amount) {
        return Settlement.builder()
                .id(id)
                .group(Group.builder().id(GROUP_ID).build())
                .paidByUser(User.builder().id(paidById).build())
                .paidToUser(User.builder().id(paidToId).build())
                .amount(amount)
                .build();
    }

    private SettlementRequest createRequest() {
        return SettlementRequest.builder()
                .groupId(GROUP_ID)
                .debtorId(DEBTOR_ID)
                .creditorId(CREDITOR_ID)
                .amount(AMOUNT)
                .build();
    }

    @Test
    void createPayment_shouldCreateSettlementWhenValid() {
        SettlementRequest request = createRequest();
        GroupBalance debtorBalance = createGroupBalance(DEBTOR_ID, new BigDecimal("-100.00"));
        GroupBalance creditorBalance = createGroupBalance(CREDITOR_ID, new BigDecimal("100.00"));

        when(groupBalanceRepository.findAllWithUsersByGroupId(GROUP_ID))
                .thenReturn(List.of(debtorBalance, creditorBalance));
        when(settlementRepository.save(any(Settlement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SettlementResponse response = settlementService.createPayment(request);

        assertNotNull(response);
        verify(groupBalanceRepository).updateBalance(GROUP_ID, DEBTOR_ID, AMOUNT);
        verify(groupBalanceRepository).updateBalance(GROUP_ID, CREDITOR_ID, AMOUNT.negate());
        verify(settlementRepository).save(any(Settlement.class));
    }

    @Test
    void createPayment_shouldThrowWhenUsersNotInGroup() {
        SettlementRequest request = createRequest();
        when(groupBalanceRepository.findAllWithUsersByGroupId(GROUP_ID)).thenReturn(List.of());

        assertThrows(GroupBalanceNotFoundException.class,
                () -> settlementService.createPayment(request));
    }

    @Test
    void createPayment_shouldThrowWhenDebtorDoesntOwe() {
        SettlementRequest request = createRequest();
        GroupBalance debtorBalance = createGroupBalance(DEBTOR_ID, BigDecimal.ZERO);
        GroupBalance creditorBalance = createGroupBalance(CREDITOR_ID, new BigDecimal("100.00"));

        when(groupBalanceRepository.findAllWithUsersByGroupId(GROUP_ID))
                .thenReturn(List.of(debtorBalance, creditorBalance));

        assertThrows(InvalidPaymentException.class,
                () -> settlementService.createPayment(request));
    }

    @Test
    void createPayment_shouldThrowWhenCreditorNotOwed() {
        SettlementRequest request = createRequest();
        GroupBalance debtorBalance = createGroupBalance(DEBTOR_ID, new BigDecimal("-100.00"));
        GroupBalance creditorBalance = createGroupBalance(CREDITOR_ID, BigDecimal.ZERO);

        when(groupBalanceRepository.findAllWithUsersByGroupId(GROUP_ID))
                .thenReturn(List.of(debtorBalance, creditorBalance));

        assertThrows(InvalidPaymentException.class,
                () -> settlementService.createPayment(request));
    }

    @Test
    void createPayment_shouldThrowWhenAmountExceedsMax() {
        SettlementRequest request = createRequest();
        request.setAmount(new BigDecimal("150.00"));
        GroupBalance debtorBalance = createGroupBalance(DEBTOR_ID, new BigDecimal("-100.00"));
        GroupBalance creditorBalance = createGroupBalance(CREDITOR_ID, new BigDecimal("100.00"));

        when(groupBalanceRepository.findAllWithUsersByGroupId(GROUP_ID))
                .thenReturn(List.of(debtorBalance, creditorBalance));

        assertThrows(InvalidPaymentException.class,
                () -> settlementService.createPayment(request));
    }

    @Test
    void updateSettlement_shouldUpdateWhenValid() {
        Long settlementId = 1L;
        SettlementRequest request = createRequest();
        BigDecimal originalAmount = new BigDecimal("30.00");
        Settlement existingSettlement = createSettlement(settlementId, DEBTOR_ID, CREDITOR_ID, originalAmount);

        GroupBalance debtorBalance = createGroupBalance(DEBTOR_ID, new BigDecimal("-100.00"));
        GroupBalance creditorBalance = createGroupBalance(CREDITOR_ID, new BigDecimal("100.00"));

        when(settlementRepository.findById(settlementId)).thenReturn(Optional.of(existingSettlement));
        when(groupBalanceRepository.findAllWithUsersByGroupId(GROUP_ID))
                .thenReturn(List.of(debtorBalance, creditorBalance));
        when(settlementRepository.save(any(Settlement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SettlementResponse response = settlementService.updateSettlement(settlementId, request, authenticatedUser);

        assertNotNull(response);

        verify(groupBalanceRepository).updateBalance(GROUP_ID, DEBTOR_ID, originalAmount.negate());
        verify(groupBalanceRepository).updateBalance(GROUP_ID, CREDITOR_ID, originalAmount);

        verify(groupBalanceRepository).updateBalance(GROUP_ID, DEBTOR_ID, AMOUNT);
        verify(groupBalanceRepository).updateBalance(GROUP_ID, CREDITOR_ID, AMOUNT.negate());

        verify(settlementRepository).save(existingSettlement);
        assertEquals(AMOUNT, existingSettlement.getAmount());
    }

    @Test
    void updateSettlement_shouldThrowWhenNotAuthorized() {
        Long settlementId = 1L;
        User otherUser = User.builder().id(999L).build();
        Settlement existingSettlement = createSettlement(settlementId, DEBTOR_ID, CREDITOR_ID, AMOUNT);

        when(settlementRepository.findById(settlementId)).thenReturn(Optional.of(existingSettlement));

        assertThrows(ActionIsNotAllowedException.class,
                () -> settlementService.updateSettlement(settlementId, createRequest(), otherUser));
    }

    @Test
    void deleteSettlement_shouldDeleteWhenValid() {
        Long settlementId = 1L;
        Settlement settlement = createSettlement(settlementId, DEBTOR_ID, CREDITOR_ID, AMOUNT);

        when(settlementRepository.findById(settlementId)).thenReturn(Optional.of(settlement));

        settlementService.deleteSettlement(settlementId, authenticatedUser);

        verify(groupBalanceRepository).updateBalance(GROUP_ID, DEBTOR_ID, AMOUNT.negate());
        verify(groupBalanceRepository).updateBalance(GROUP_ID, CREDITOR_ID, AMOUNT);
        verify(settlementRepository).delete(settlement);
    }

    @Test
    void deleteSettlement_shouldThrowWhenNotAuthorized() {
        Long settlementId = 1L;
        User otherUser = User.builder().id(999L).build();
        Settlement settlement = createSettlement(settlementId, DEBTOR_ID, CREDITOR_ID, AMOUNT);

        when(settlementRepository.findById(settlementId)).thenReturn(Optional.of(settlement));

        assertThrows(ActionIsNotAllowedException.class,
                () -> settlementService.deleteSettlement(settlementId, otherUser));
    }

    @Test
    void getGroupPayments_shouldReturnPayments() {
        Settlement settlement1 = createSettlement(1L, DEBTOR_ID, CREDITOR_ID, AMOUNT);
        Settlement settlement2 = createSettlement(2L, CREDITOR_ID, DEBTOR_ID, AMOUNT);

        when(settlementRepository.findByGroup_Id(GROUP_ID)).thenReturn(List.of(settlement1, settlement2));

        List<SettlementResponse> responses = settlementService.getGroupPayments(GROUP_ID);

        assertEquals(2, responses.size());
        verify(settlementRepository).findByGroup_Id(GROUP_ID);
    }
}