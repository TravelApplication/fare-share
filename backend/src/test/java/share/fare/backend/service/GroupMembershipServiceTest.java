package share.fare.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import share.fare.backend.dto.response.GroupMembershipResponse;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupMembership;
import share.fare.backend.entity.GroupRole;
import share.fare.backend.entity.User;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.exception.UserAlreadyInGroupException;
import share.fare.backend.exception.UserIsNotInGroupException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.repository.GroupMembershipRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupMembershipServiceTest {
    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupMembershipService groupMembershipService;

    private User testUser;
    private Group testGroup;
    private GroupMembership testMembership;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("password")
                .build();

        testGroup = Group.builder()
                .id(1L)
                .name("Test Group")
                .description("Test Description")
                .build();

        testMembership = GroupMembership.builder()
                .id(1L)
                .group(testGroup)
                .user(testUser)
                .role(GroupRole.MEMBER)
                .build();
    }

    @Test
    public void testAddMemberToGroupSuccess() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupMembershipRepository.existsByGroupAndUser(testGroup, testUser)).thenReturn(false);
        when(groupMembershipRepository.save(any(GroupMembership.class))).thenReturn(testMembership);

        GroupMembershipResponse result = groupMembershipService.addMemberToGroup(1L, 1L, GroupRole.MEMBER, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(groupMembershipRepository, times(1)).existsByGroupAndUser(testGroup, testUser);
        verify(groupMembershipRepository, times(1)).save(any(GroupMembership.class));
    }

    @Test
    public void testAddMemberToGroupGroupNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupMembershipService.addMemberToGroup(1L, 1L, GroupRole.MEMBER, 1L));
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testAddMemberToGroupUserNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> groupMembershipService.addMemberToGroup(1L, 1L, GroupRole.MEMBER, 1L));
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testAddMemberToGroupUserAlreadyInGroup() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupMembershipRepository.existsByGroupAndUser(testGroup, testUser)).thenReturn(true);

        assertThrows(UserAlreadyInGroupException.class, () -> groupMembershipService.addMemberToGroup(1L, 1L, GroupRole.MEMBER, 1L));
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(groupMembershipRepository, times(1)).existsByGroupAndUser(testGroup, testUser);
    }

    @Test
    public void testRemoveMemberFromGroupSuccess() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupMembershipRepository.findByGroupAndUser(testGroup, testUser)).thenReturn(Optional.of(testMembership));
        doNothing().when(groupMembershipRepository).delete(testMembership);

        groupMembershipService.removeMemberFromGroup(1L, 1L);

        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(groupMembershipRepository, times(1)).findByGroupAndUser(testGroup, testUser);
        verify(groupMembershipRepository, times(1)).delete(testMembership);
    }

    @Test
    public void testRemoveMemberFromGroupGroupNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupMembershipService.removeMemberFromGroup(1L, 1L));
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testRemoveMemberFromGroupUserNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> groupMembershipService.removeMemberFromGroup(1L, 1L));
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testRemoveMemberFromGroupUserNotInGroup() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupMembershipRepository.findByGroupAndUser(testGroup, testUser)).thenReturn(Optional.empty());

        assertThrows(UserIsNotInGroupException.class, () -> groupMembershipService.removeMemberFromGroup(1L, 1L));
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(groupMembershipRepository, times(1)).findByGroupAndUser(testGroup, testUser);
    }

    @Test
    public void testRemoveMemberFromGroupOwnerCannotBeRemoved() {
        testMembership.setRole(GroupRole.OWNER);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupMembershipRepository.findByGroupAndUser(testGroup, testUser)).thenReturn(Optional.of(testMembership));

        assertThrows(IllegalArgumentException.class, () -> groupMembershipService.removeMemberFromGroup(1L, 1L));
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(groupMembershipRepository, times(1)).findByGroupAndUser(testGroup, testUser);
    }

    @Test
    public void testUpdateMemberRoleSuccess() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupMembershipRepository.findByGroupAndUser(testGroup, testUser)).thenReturn(Optional.of(testMembership));
        when(groupMembershipRepository.save(any(GroupMembership.class))).thenReturn(testMembership);

        GroupMembershipResponse result = groupMembershipService.updateMemberRole(1L, 1L, GroupRole.ADMIN);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(groupMembershipRepository, times(1)).findByGroupAndUser(testGroup, testUser);
        verify(groupMembershipRepository, times(1)).save(any(GroupMembership.class));
    }

    @Test
    public void testUpdateMemberRoleGroupNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupMembershipService.updateMemberRole(1L, 1L, GroupRole.ADMIN));
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateMemberRoleUserNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> groupMembershipService.updateMemberRole(1L, 1L, GroupRole.ADMIN));
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateMemberRoleUserNotInGroup() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupMembershipRepository.findByGroupAndUser(testGroup, testUser)).thenReturn(Optional.empty());

        assertThrows(UserIsNotInGroupException.class, () -> groupMembershipService.updateMemberRole(1L, 1L, GroupRole.ADMIN));
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(groupMembershipRepository, times(1)).findByGroupAndUser(testGroup, testUser);
    }

    @Test
    public void testGetGroupMembersSuccess() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(groupMembershipRepository.findByGroup(testGroup)).thenReturn(Collections.singletonList(testMembership));

        List<GroupMembershipResponse> result = groupMembershipService.getGroupMembers(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getUserId());
        verify(groupRepository, times(1)).findById(1L);
        verify(groupMembershipRepository, times(1)).findByGroup(testGroup);
    }

    @Test
    public void testGetGroupMembersGroupNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupMembershipService.getGroupMembers(1L));
        verify(groupRepository, times(1)).findById(1L);
    }

}