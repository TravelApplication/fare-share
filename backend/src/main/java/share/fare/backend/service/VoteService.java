package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.request.VoteRequest;
import share.fare.backend.dto.response.VoteResponse;
import share.fare.backend.entity.Activity;
import share.fare.backend.entity.User;
import share.fare.backend.entity.Vote;
import share.fare.backend.exception.*;
import share.fare.backend.mapper.VoteMapper;
import share.fare.backend.repository.ActivityRepository;
import share.fare.backend.repository.GroupMembershipRepository;
import share.fare.backend.repository.UserRepository;
import share.fare.backend.repository.VoteRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    public VoteResponse addVote(Long activityId, Long userId, VoteRequest voteRequest) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException("Activity not found with ID: " + activityId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (voteRepository.existsByActivityAndUser(activity, user)) {
            throw new DuplicateVoteException("User has already voted on this activity");
        }

        if (!groupMembershipRepository.existsByGroupAndUser(activity.getGroup(), user)) {
            throw new UserIsNotInGroupException("User with ID: " + userId + " is not a member of the group");
        }

        Vote vote = VoteMapper.toEntity(voteRequest, activity, user);
        Vote savedVote = voteRepository.save(vote);
        return VoteMapper.toResponse(savedVote);
    }

    public List<VoteResponse> getVotesForActivity(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException("Activity not found with ID: " + activityId));

        return voteRepository.findByActivity(activity).stream()
                .map(VoteMapper::toResponse)
                .collect(Collectors.toList());
    }

    public VoteResponse updateVote(Long voteId, Long userId, VoteRequest request) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new VoteNotFoundException("Vote not found with ID: " + voteId));

        if (!vote.getUser().getId().equals(userId)) {
            throw new ActionIsNotAllowedException("You are not authorized to update this vote");
        }

        vote.setVoteType(request.getVoteType());
        Vote updatedVote = voteRepository.save(vote);
        return VoteMapper.toResponse(updatedVote);
    }

    public void deleteVote(Long voteId, Long userId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new VoteNotFoundException("Vote not found with ID: " + voteId));

        if (!vote.getUser().getId().equals(userId)) {
            throw new ActionIsNotAllowedException("You are not authorized to delete this vote");
        }

        voteRepository.delete(vote);
    }
}