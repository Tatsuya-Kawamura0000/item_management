package com.example.itemmanagement.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterForm {

    @NotBlank(message = "ユーザー名は必須です")
    @Size(max = 50, message = "ユーザー名は50文字以内で入力してください")
    private String loginId;

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 4, max = 20, message = "パスワードは4〜20文字で入力してください")
    private String password;

}