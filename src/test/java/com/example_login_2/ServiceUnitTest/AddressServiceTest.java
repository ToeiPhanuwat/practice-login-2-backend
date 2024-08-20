package com.example_login_2.ServiceUnitTest;

import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.Address;
import com.example_login_2.model.User;
import com.example_login_2.repository.AddressRepository;
import com.example_login_2.service.AddressServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {
    private User mockUser;
    private Address mockAddress;
    @Mock
    private AddressRepository repository;
    @InjectMocks
    private AddressServiceImp serviceImp;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(TestData.id);
        mockUser.setEmail(TestData.email);
        mockUser.setPassword(TestData.password);

        mockAddress = new Address();
        mockAddress.setId(TestData.id);
        mockAddress.setUser(mockUser);
        mockAddress.setAddress(TestData.address);

        mockUser.setAddress(mockAddress);
    }

    @Test
    public void testCreateAddress() {
        when(repository.save(any(Address.class))).thenReturn(mockAddress);

        Address address = serviceImp.createAddress(mockUser);

        assertNotNull(address);
        assertEquals(mockUser, address.getUser());
        assertEquals(mockAddress.getId(), address.getId());

        verify(repository).save(any(Address.class));
    }

    @Test
    public void testUpdateAddress() {
        UpdateRequest request = new UpdateRequest();

        when(repository.save(any(Address.class))).thenReturn(mockAddress);

        Address updatedAddress = serviceImp.updateAddress(mockUser, request);

        assertNotNull(updatedAddress);
        assertEquals(mockUser, updatedAddress.getUser());
        assertEquals(mockAddress.getId(), updatedAddress.getId());
        assertEquals(mockAddress.getAddress(), updatedAddress.getAddress());

        verify(repository).save(any(Address.class));
    }

    interface TestData {
        Long id = 1L;
        String email = "test@email.com";
        String password = "password";
        String address = "15/7";
    }
}
