package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupMembership;
import share.fare.backend.entity.GroupRole;
import share.fare.backend.entity.User;
import share.fare.backend.exception.UserIsNotGroupOwner;
import share.fare.backend.exception.UserIsNotInGroupException;
import share.fare.backend.repository.GroupMembershipRepository;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final GroupMembershipRepository membershipRepository;

    public void checkIfUserIsInGroup(User user, Group group) {
        findMembershipOrThrow(user, group);
    }

    public void checkIfUserIsGroupOwner(User user, Group group) {
        GroupMembership membership = findMembershipOrThrow(user, group);

        if (membership.getRole() != GroupRole.OWNER) {
            throw new UserIsNotGroupOwner(user.getId(), group.getId());
        }
    }

    public void checkIfUserCanRemoveMember(User currentUser, User userToRemove, Group group) {
        GroupMembership membership = findMembershipOrThrow(currentUser, group);
        if (membership.getRole() != GroupRole.OWNER && !currentUser.getId().equals(userToRemove.getId())) {
            throw new UserIsNotGroupOwner(currentUser.getId(), group.getId());
        }
    }

    private GroupMembership findMembershipOrThrow(User user, Group group) {
        return membershipRepository.findByGroupAndUser(group, user)
                .orElseThrow(() ->
                        new UserIsNotInGroupException("User " + user.getId() + " is not in group " + group.getName()));
    }
}
