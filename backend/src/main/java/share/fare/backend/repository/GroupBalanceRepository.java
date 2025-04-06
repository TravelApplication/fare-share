package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupBalance;
import share.fare.backend.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GroupBalanceRepository extends JpaRepository<GroupBalance, Long> {
    Optional<GroupBalance> findByGroupAndUser(Group group, User paidByUser);

    List<GroupBalance> findByGroup(Group group);

    @Query("SELECT gb FROM group_balance gb " +
            "JOIN FETCH gb.user u " +
            "WHERE gb.group.id = :groupId AND u.id IN (:userIds)")
    List<GroupBalance> findWithUsersByGroupAndUserIds(
            @Param("groupId") Long groupId,
            @Param("userIds") List<Long> userIds);

    @Modifying
    @Query("UPDATE group_balance gb SET gb.balance = gb.balance + :amount " +
            "WHERE gb.group.id = :groupId AND gb.user.id = :userId")
    void updateBalance(@Param("groupId") Long groupId,
                       @Param("userId") Long userId,
                       @Param("amount") BigDecimal amount);

    @Query("SELECT gb FROM group_balance gb JOIN FETCH gb.user WHERE gb.group.id = :groupId")
    List<GroupBalance> findAllWithUsersByGroupId(@Param("groupId") Long groupId);
}
