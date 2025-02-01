package share.fare.backend.mapper;

import share.fare.backend.dto.request.VoteRequest;
import share.fare.backend.dto.response.VoteResponse;
import share.fare.backend.entity.Activity;
import share.fare.backend.entity.User;
import share.fare.backend.entity.Vote;

public class VoteMapper {
    public static VoteResponse toResponse(Vote vote) {
        if (vote == null) {
            return null;
        }

        return VoteResponse.builder()
                .id(vote.getId())
                .userId(vote.getUser() != null ? vote.getUser().getId() : null)
                .userEmail(vote.getUser() != null ? vote.getUser().getEmail() : null)
                .activityId(vote.getActivity() != null ? vote.getActivity().getId() : null)
                .voteType(vote.getVoteType())
                .build();
    }

    public static Vote toEntity(VoteRequest request, Activity activity, User user) {
        if (request == null) {
            return null;
        }

        return Vote.builder()
                .voteType(request.getVoteType())
                .activity(activity)
                .user(user)
                .build();
    }
}
