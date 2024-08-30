package com.example_login_2.serviceUnit_unit_test;

import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.Address;
import com.example_login_2.model.User;
import com.example_login_2.repository.AdminRepository;
import com.example_login_2.service.AdminServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private AdminRepository repository;

    @InjectMocks
    private AdminServiceImp serviceImp;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(TestData.id);
        mockUser.setEmail(TestData.email);
        mockUser.setPassword(TestData.password);
        mockUser.setFirstName(TestData.firstname);
        mockUser.setLastName(TestData.lastname);
    }

    @Test
    public void testGetAllUsers() {
        when(repository.findAll()).thenReturn(Collections.singletonList(mockUser));

        List<User> result = serviceImp.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(mockUser.getId(), result.get(0).getId());

        verify(repository, times(1)).findAll();
    }

    @Test
    public void testGetUserById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        User user = serviceImp.getUserById(TestData.id).orElse(null);

        assertNotNull(user);
        assertEquals(mockUser.getEmail(), user.getEmail());

        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdateUserRequest() {
        when(repository.save(any(User.class))).thenReturn(mockUser);

        UpdateRequest request = new UpdateRequest();
        User user = serviceImp.updateUserRequest(mockUser, request);

        assertNotNull(user);
        assertEquals(mockUser.getFileName(), user.getFileName());

        verify(repository).save(any(User.class));
    }

    @Test
    public void testUpdateAddress() {
        Address address = new Address()
                .setAddress("555");
        when(repository.save(any(User.class))).thenReturn(mockUser);

        User user = serviceImp.updateAddress(mockUser, address);

        assertNotNull(user);
        assertNotNull(user.getAddress());
        assertEquals(mockUser.getAddress().getAddress(), user.getAddress().getAddress());
        verify(repository).save(any(User.class));
    }

    @Test
    public void testRemoveRoleAndUpdate() {
        when(repository.save(any(User.class))).thenReturn(mockUser);

        RoleUpdateRequest request = new RoleUpdateRequest();
        User user = serviceImp.removeRoleAndUpdate(mockUser, request);

        assertNotNull(user);
        assertEquals(0, user.getRoles().size());

        verify(repository).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        serviceImp.deleteUser(anyLong());

        verify(repository).deleteById(anyLong());
    }

    @Test
    public void testSearchRoleUser() {
        when(repository.findByRoles(anyString())).thenReturn(Collections.singletonList(mockUser));

        mockUser.setRoles(new HashSet<>(Collections.singleton(TestData.Role)));

        List<User> result = serviceImp.searchRoleUser(TestData.Role);

        assertEquals(1, result.size());
        assertEquals(TestData.Role, result.get(0).getRoles().iterator().next());

        verify(repository).findByRoles(anyString());
    }


    interface TestData {
        Long id = 1L;
        String email = "test@email.com";
        String password = "password";
        String firstname = "Tonson";
        String lastname = "Sungharut";
        String Role = "ROLE_ADMIN";
    }
}
