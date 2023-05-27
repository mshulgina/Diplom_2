package userTests;

import baseUtil.ClientCredentials;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserClient;
import user.UserInfo;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DisplayName("Авторизация пользователя")
public class AuthorizationUserTest {
    private User user;
    private User userIncorrect;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserInfo.getUser();
        userIncorrect = UserInfo.getUserIncorrect();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Авторизация пользователя с корректными данными.")
    @Description("Авторизация пользователя с корректными данными. Проверка успешного ответа сервера.")
    public void authUserTest() {
        userClient.createUser(ClientCredentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(ClientCredentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        assertEquals(SC_OK, responseLogin.extract().statusCode());
        assertTrue(responseLogin.extract().path("success"));
    }

    @Test
    @DisplayName("Авторизация с неверным логином и/или паролем")
    @Description("Авторизация с неверным логином и/или паролем. Проверка неуспешного ответа сервера.")
    public void authUserWithIncorrectCredentialsTest() {
        ValidatableResponse responseCreate = userClient.createUser(ClientCredentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(ClientCredentials.from(userIncorrect));
        accessToken = responseCreate.extract().path("accessToken");
        assertEquals(SC_UNAUTHORIZED, responseLogin.extract().statusCode());
        assertEquals("email or password are incorrect", responseLogin.extract().path("message"));
    }

    @Test
    @DisplayName("Авторизация с пустым полем пароля")
    @Description("Авторизация с пустым полем пароля. Проверка неуспешного ответа сервера.")
    public void authUserWithEmptyPasswordTest() {
        ValidatableResponse responseCreate = userClient.createUser(ClientCredentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(ClientCredentials.fromOnlyEmail(user));
        accessToken = responseCreate.extract().path("accessToken");
        assertEquals(SC_UNAUTHORIZED, responseLogin.extract().statusCode());
        assertEquals("email or password are incorrect", responseLogin.extract().path("message"));
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
