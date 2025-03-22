package share.fare.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import share.fare.backend.dto.response.GroupMembershipResponse;
import share.fare.backend.service.GroupMembershipService;
import share.fare.backend.service.JwtService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final GroupMembershipService groupMembershipService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        try {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                handleConnect(accessor);
            }
            else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                handleSubscribe(accessor);
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                handleDisconnect(accessor);
            }
        } catch (Exception e) {
            throw new SecurityException("WebSocket operation not allowed", e);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String userId = jwtService.extractUsername(token);
            if (userId != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    accessor.setUser(authentication);
                    accessor.getSessionAttributes().put("authentication", authentication);
                } else {
                    throw new SecurityException("Invalid token");
                }
            }
        } else {
            throw new SecurityException("Authorization header missing or invalid");
        }
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        Authentication authentication = (Authentication) accessor.getSessionAttributes().get("authentication");
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        String destination = accessor.getDestination();
        if (destination != null) {
            if (destination.startsWith("/user/")) {
                String userIdFromDestination = destination.split("/")[2];
                String authenticatedUserId = authentication.getName();
                if (!userIdFromDestination.equals(authenticatedUserId)) {
                    throw new SecurityException("User not authorized to subscribe to this channel");
                }
            } else if (destination.startsWith("/group/")) {
                String groupId = destination.split("/")[2];
                String authenticatedUserId = authentication.getName();
                List<GroupMembershipResponse> groupUsers = groupMembershipService
                        .getGroupMembers(Long.parseLong(groupId));
                boolean userIsInGroup = groupUsers.stream()
                        .anyMatch(g -> g.getUserId().equals(Long.parseLong(authenticatedUserId)));
                if (groupUsers.isEmpty() || !userIsInGroup) {
                    throw new SecurityException("User not authorized to subscribe to this channel");
                }
            }
        }
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        accessor.getSessionAttributes().remove("authentication");
    }
}