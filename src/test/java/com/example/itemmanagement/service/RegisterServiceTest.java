package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Users;
import com.example.itemmanagement.form.RegisterForm;
import com.example.itemmanagement.mapper.UsersMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UsersMapper usersMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterService sut;


    @Test
    void register_取得したパスワードを暗号化しユーザー登録できること() {

        //Arrange
        RegisterForm form = createRegisterForm();

        when(passwordEncoder.encode("rawPassword"))
                .thenReturn("encodedPassword");

        //Act
        sut.register(form);

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);

        //Assert
        verify(passwordEncoder, times(1)).encode(form.getPassword());
        verify(usersMapper, times(1)).insertUser(captor.capture());

        Users actual = captor.getValue();

        assertEquals(form.getLoginId(), actual.getLoginId());
        assertEquals("encodedPassword", actual.getPassword());    //パスワードが暗号化されていることを確認
        assertEquals(form.getEmail(), actual.getEmail());

    }

    @Test
    void register_ユーザー登録時に例外が発生した場合_RuntimeExceptionを送出すること() {

        //Arrange
        RegisterForm form = createRegisterForm();

        doThrow(new RuntimeException()).when(usersMapper).insertUser(any(Users.class));

        //Act & Assert
        assertThrows(RuntimeException.class, () -> sut.register(form));

        verify(usersMapper, times(1)).insertUser(any(Users.class));

    }

    @Test
    void register_パスワード暗号化時に例外が発生した場合_RuntimeExceptionを送出すること() {

        //Arrange
        RegisterForm form = createRegisterForm();

        when(passwordEncoder.encode(form.getPassword()))
                .thenThrow(new RuntimeException());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> sut.register(form));

        verify(passwordEncoder, times(1)).encode(form.getPassword());
        verify(usersMapper, times(0)).insertUser(any(Users.class));

    }


    private RegisterForm createRegisterForm() {              //共通メソッド

        RegisterForm form = new RegisterForm();

        form.setLoginId("testUser");
        form.setPassword("rawPassword");
        form.setEmail("test@example.com");

        return form;

    }

}


