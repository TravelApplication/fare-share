package share.fare.backend.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import share.fare.backend.dto.request.GroupRequest;
import share.fare.backend.dto.response.GroupResponse;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.User;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupService groupService;

    private User testUser;
    private Group testGroup;
    private GroupRequest testGroupRequest;

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
                .tripStartDate(LocalDate.now().plusDays(1))
                .tripEndDate(LocalDate.now().plusDays(5))
                .createdBy(testUser)
                .build();

        testGroupRequest = GroupRequest.builder()
                .name("Test Group")
                .description("Test Description")
                .tripStartDate(LocalDate.now().plusDays(1))
                .tripEndDate(LocalDate.now().plusDays(5))
                .build();
    }

    @Test
    public void testCreateGroup() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);

        GroupResponse result = groupService.createGroup(testGroupRequest, 1L);

        assertNotNull(result);
        assertEquals("Test Group", result.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    public void testCreateGroupUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> groupService.createGroup(testGroupRequest, 1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateGroupInvalidTripDates() {
        GroupRequest invalidRequest = GroupRequest.builder()
                .name("Invalid Group")
                .description("Invalid Description")
                .tripStartDate(LocalDate.now().plusDays(5))
                .tripEndDate(LocalDate.now().plusDays(1))
                .build();


        assertThrows(IllegalArgumentException.class, () -> groupService.createGroup(invalidRequest, 1L));
    }

    @Test
    public void testGetAllGroups() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Group> groupPage = new PageImpl<>(Collections.singletonList(testGroup), pageable, 1);
        when(groupRepository.findAll(pageable)).thenReturn(groupPage);

        Page<GroupResponse> result = groupService.getAllGroups(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(groupRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetGroupByIdSuccess() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        GroupResponse result = groupService.getGroupById(1L);

        assertNotNull(result);
        assertEquals("Test Group", result.getName());
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetGroupById_NotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.getGroupById(1L));
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetGroupsForUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Group> groupPage = new PageImpl<>(Collections.singletonList(testGroup), pageable, 1);
        when(groupRepository.findByCreatedByIdOrMembershipsUser_Id(1L, pageable)).thenReturn(groupPage);

        Page<GroupResponse> result = groupService.getGroupsForUser(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(groupRepository, times(1)).findByCreatedByIdOrMembershipsUser_Id(1L, pageable);
    }

    @Test
    public void testDeleteGroupSuccess() {
        when(groupRepository.existsById(1L)).thenReturn(true);
        doNothing().when(groupRepository).deleteById(1L);

        groupService.deleteGroup(1L);

        verify(groupRepository, times(1)).existsById(1L);
        verify(groupRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteGroupNotFound() {
        when(groupRepository.existsById(1L)).thenReturn(false);

        assertThrows(GroupNotFoundException.class, () -> groupService.deleteGroup(1L));
        verify(groupRepository, times(1)).existsById(1L);
    }

    @Test
    public void testUpdateGroupSuccess() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);

        GroupResponse result = groupService.updateGroup(1L, testGroupRequest);

        assertNotNull(result);
        assertEquals("Test Group", result.getName());
        verify(groupRepository, times(1)).findById(1L);
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    public void testUpdateGroupNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.updateGroup(1L, testGroupRequest));
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateGroupInvalidTripDates() {
        GroupRequest invalidRequest = GroupRequest.builder()
                .name("Invalid Group")
                .description("Invalid Description")
                .tripStartDate(LocalDate.now().plusDays(5))
                .tripEndDate(LocalDate.now().plusDays(1))
                .build();

        assertThrows(IllegalArgumentException.class, () -> groupService.updateGroup(1L, invalidRequest));
    }
}