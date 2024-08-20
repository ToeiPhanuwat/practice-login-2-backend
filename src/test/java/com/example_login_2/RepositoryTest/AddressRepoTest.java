package com.example_login_2.RepositoryTest;

import com.example_login_2.model.Address;
import com.example_login_2.repository.AddressRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AddressRepoTest {

    @Autowired
    private AddressRepository repository;

    @Test
    public void testSave() {
        Address address = new Address();
        Address saved = repository.save(address);

        assertNotNull(saved);
        assertNotNull(address.getId());
    }
}
