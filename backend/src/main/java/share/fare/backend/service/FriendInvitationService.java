package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.fare.backend.dto.response.FriendInvitationResponse;
import share.fare.backend.entity.FriendInvitation;
import share.fare.backend.entity.Friendship;
import share.fare.backend.entity.FriendshipId;
import share.fare.backend.entity.User;
import share.fare.backend.exception.InvitationAlreadyExistsException;
import share.fare.backend.exception.InvitationNotFoundException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.mapper.FriendInvitationMapper;
import share.fare.backend.repository.FriendInvitationRepository;
import share.fare.backend.repository.FriendshipRepository;
import share.fare.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendInvitationService {

    private final UserRepository userRepository;

    private final FriendInvitationRepository invitationRepository;

    private final FriendshipRepository friendshipRepository;

    @Transactional
    public FriendInvitationResponse sendFriendInvitation(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("You cannot send a friend invitation to yourself.");
        }

        if (invitationRepository.existsBySenderIdAndReceiverId(senderId, receiverId)) {
            throw new InvitationAlreadyExistsException("Friend invitation already exists.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException(senderId));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException(receiverId));

        FriendInvitation invitation = FriendInvitation.builder()
                .sender(sender)
                .receiver(receiver)
                .build();

        invitationRepository.save(invitation);

        return FriendInvitationMapper.toResponse(invitation);
    }

    public List<FriendInvitationResponse> getSentFriendInvitations(Long userId) {
        List<FriendInvitation> sentFriendInvitations = invitationRepository.findBySenderId(userId);
        return sentFriendInvitations.stream()
                .map(FriendInvitationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<FriendInvitationResponse> getReceivedFriendInvitations(Long userId) {
        List<FriendInvitation> receivedFriendInvitations = invitationRepository.findByReceiverId(userId);
        return receivedFriendInvitations.stream()
                .map(FriendInvitationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptFriendInvitation(Long invitationId, Long userId) {
        FriendInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException("Friend Invitation not found"));

        if (!invitation.getReceiver().getId().equals(userId)) {
            throw new InvitationNotFoundException("User is not the receiver of this invitation");
        }

        Friendship friendship = Friendship.builder()
                .user1(invitation.getSender())
                .user2(invitation.getReceiver())
                .id(new FriendshipId(invitation.getSender().getId(), invitation.getReceiver().getId()))
                .build();

        friendshipRepository.save(friendship);

        invitationRepository.delete(invitation);
    }

    @Transactional
    public void rejectFriendInvitation(Long invitationId, Long userId) {
        FriendInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException("Friend Invitation not found"));

        if (!invitation.getReceiver().getId().equals(userId)) {
            throw new InvitationNotFoundException("User is not the receiver of this invitation");
        }

        invitationRepository.delete(invitation);
    }

}
