package hu.tilos.radio.backend.auth;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RegisterData {

    @NotNull
    @Pattern(regexp = "[A-Za-z0-9\\-]+")
    @Size(min = 5, max = 16)
    private String username;

    @NotNull
    @Size(min = 8, max = 16)
    private String password;

    @NotNull
    private String email;

    @NotNull
    private String captcha;

    public RegisterData() {
    }

    public RegisterData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
