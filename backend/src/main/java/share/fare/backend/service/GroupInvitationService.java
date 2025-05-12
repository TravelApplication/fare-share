package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.fare.backend.dto.response.GroupInvitationResponse;
import share.fare.backend.entity.*;
import share.fare.backend.exception.*;
import share.fare.backend.mapper.GroupInvitationMapper;
import share.fare.backend.repository.GroupInvitationRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;
import share.fare.backend.util.GroupInvitationNotification;
import share.fare.backend.util.NotificationType;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupInvitationService {

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final GroupInvitationRepository invitationRepository;

    private final NotificationService notificationService;

    @Transactional
    public GroupInvitationResponse sendGroupInvitation(Long senderId, Long receiverId, Long groupId) {
        if (senderId.equals(receiverId)) {
            throw new ActionIsNotAllowedException("You cannot send a group invitation to yourself.");
        }

        if (invitationRepository.existsBySenderIdAndReceiverIdAndGroupId(senderId, receiverId, groupId)) {
            throw new InvitationAlreadyExistsException("Group invitation already exists.");
        }
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException(senderId));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException(receiverId));

        if (!group.getMemberships().stream()
                .map(GroupMembership::getUser).toList().contains(sender)) {
            throw new UserIsNotInGroupException("Sender does not belong to group.");
        }

        GroupInvitation invitation = GroupInvitation.builder()
                .sender(sender)
                .receiver(receiver)
                .group(group)
                .build();

        invitationRepository.save(invitation);

        notificationService.sendNotificationToUser(receiverId, GroupInvitationNotification.builder()
                        .senderId(senderId)
                        .senderEmail(receiver.getEmail())
                        .groupId(groupId)
                        .groupName(group.getName())
                        .type(NotificationType.GROUP_INVITATION)
                        .message("You received invitation to group " + group.getName() + " from " + sender.getEmail())
                .build());

        return GroupInvitationMapper.toResponse(invitation);
    }

    public List<GroupInvitationResponse> getSentGroupInvitations(Long userId) {
        List<GroupInvitation> sentGroupInvitations = invitationRepository.findBySenderId(userId);
        return sentGroupInvitations.stream()
                .map(GroupInvitationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<GroupInvitationResponse> getReceivedGroupInvitations(Long userId) {
        List<GroupInvitation> receivedGroupInvitations = invitationRepository.findByReceiverId(userId);
        return receivedGroupInvitations.stream()
                .map(GroupInvitationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptGroupInvitation(Long invitationId, Long userId) {
        GroupInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException("Group invitation not found"));

        if (!invitation.getReceiver().getId().equals(userId)) {
            throw new ActionIsNotAllowedException("User is not the intended receiver of this invitation");
        }

        Group group = invitation.getGroup();
        User receiver = invitation.getReceiver();
        User sender = invitation.getSender();

        group.addMember(receiver, GroupRole.MEMBER);

        invitationRepository.deleteAllByGroupIdAndReceiverId(group.getId(), receiver.getId());

        groupRepository.save(group);

        invitationRepository.delete(invitation);

        notificationService.sendNotificationToUser(sender.getId(), GroupInvitationNotification.builder()
                .senderId(receiver.getId())
                .senderEmail(receiver.getEmail())
                .type(NotificationType.GROUP_INVITATION_ACCEPT)
                .groupId(group.getId())
                .groupName(group.getName())
                .message(receiver.getEmail() + " accepted your invitation to " + group.getName())
                .build());
    }


    @Transactional
    public void rejectGroupInvitation(Long invitationId, Long userId) {
        GroupInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException("Group invitation not found"));

        if (!invitation.getReceiver().getId().equals(userId)) {
            throw new ActionIsNotAllowedException("User is not the intended receiver of this invitation");
        }

        Group group = invitation.getGroup();
        User receiver = invitation.getReceiver();
        User sender = invitation.getSender();

        invitationRepository.delete(invitation);

        notificationService.sendNotificationToUser(sender.getId(), GroupInvitationNotification.builder()
                .senderId(receiver.getId())
                .senderEmail(receiver.getEmail())
                .groupId(group.getId())
                .groupName(group.getName())
                .type(NotificationType.GROUP_INVITATION_REJECT)
                .message(receiver.getEmail() + " rejected your invitation to " + group.getName())
                .build());
    }
}
