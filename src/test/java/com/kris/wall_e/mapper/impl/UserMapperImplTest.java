package com.kris.wall_e.mapper.impl;

import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class UserMapperImplTest {

//    Arrange-Act-Assert (AAA) Pattern:
//  Clearly structure your test into three sections:
//  Arrange: Set up data and mock dependencies.
//  Act: Call the method under test.
//  Assert: Verify the result or behavior

    private final UserMapperImpl mapper = new UserMapperImpl();

    @Test
    void fromUserShouldMap_ToUserResponseDto() {

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");

        UserResponseDto result = mapper.fromUser(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.userId());
        assertEquals(user.getName(), result.name());
        assertEquals(user.getEmail(), result.email());
    }

    @Test
    void fromUserShouldReturnNull_WhenUserIsNull() {

        UserResponseDto result = mapper.fromUser(null);

        assertNull(result);
    }

}
