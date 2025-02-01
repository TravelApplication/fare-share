package share.fare.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.fare.backend.dto.response.GroupInvitationResponse;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupInvitation;
import share.fare.backend.entity.GroupRole;
import share.fare.backend.entity.User;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.exception.InvitationAlreadyExistsException;
import share.fare.backend.exception.InvitationNotFoundException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.mapper.GroupInvitationMapper;
import share.fare.backend.repository.GroupInvitationRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupInvitationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupInvitationRepository invitationRepository;

    @Transactional
    public GroupInvitationResponse sendGroupInvitation(Long senderId, Long receiverId, Long groupId) {

        if (invitationRepository.existsBySenderIdAndReceiverIdAndGroupId(senderId, receiverId, groupId)) {
            throw new InvitationAlreadyExistsException("Group invitation already exists.");
        }
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + senderId + " not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + receiverId + " not found"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group with ID " + groupId + " not found"));


        GroupInvitation invitation = GroupInvitation.builder()
                .sender(sender)
                .receiver(receiver)
                .group(group)
                .build();

        invitationRepository.save(invitation);

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
            throw new InvitationNotFoundException("User is not the intended receiver of this invitation");
        }

        Group group = invitation.getGroup();
        User receiver = invitation.getReceiver();

        group.addMember(receiver, GroupRole.MEMBER);

        invitationRepository.deleteAllByGroupIdAndReceiverId(group.getId(), receiver.getId());

        groupRepository.save(group);

        invitationRepository.delete(invitation);
    }


    @Transactional
    public void rejectGroupInvitation(Long invitationId, Long userId) {
        GroupInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException("Group invitation not found"));

        if (!invitation.getReceiver().getId().equals(userId)) {
            throw new InvitationNotFoundException("User is not the intended receiver of this invitation");
        }

        invitationRepository.delete(invitation);
    }
}
