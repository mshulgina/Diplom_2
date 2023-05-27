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
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DisplayName("Изменение данных пользователя")
public class ChangeDataUserTest {
    private User user;
    private User userUpdate;
    private User userSecond;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserInfo.getUser();
        userUpdate = UserInfo.getUserUpdate();
        userSecond = UserInfo.getUserSecond();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией.")
    @Description("Изменение имени пользователя с авторизацией. Проверка успешного ответа сервера.")
    public void changeUserDataTest() {
        userClient.createUser(ClientCredentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(ClientCredentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        ValidatableResponse responseUpdateUser = userClient.changeUser(accessToken, ClientCredentials.from(userUpdate));
        assertEquals(SC_OK, responseUpdateUser.extract().statusCode());
        assertTrue(responseUpdateUser.extract().path("success"));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Изменение данных пользователя без авторизации. Проверка неуспешного ответа от сервера.")
    public void changeUserDataWithoutLoginTest() {
        ValidatableResponse responseCreate = userClient.createUser(ClientCredentials.from(user));
        accessToken = responseCreate.extract().path("accessToken");
        ValidatableResponse responseUpdateUser = userClient.changeUser("", ClientCredentials.from(userUpdate));
        assertEquals(SC_UNAUTHORIZED, responseUpdateUser.extract().statusCode());
        assertEquals("You should be authorised", responseUpdateUser.extract().path("message"));
    }

    @Test
    @DisplayName("Изменение данных пользователя с существующим email")
    @Description("Изменение данных пользователя с существующим email. Проверка неуспешного ответа от сервера.")
    public void changeUserDataExistEmailTest() {
        ValidatableResponse responseCreate = userClient.createUser(ClientCredentials.from(user));
        ValidatableResponse responseCreateSecondUser = userClient.createUser(ClientCredentials.from(userSecond));
        accessToken = responseCreate.extract().path("accessToken");
        String accessTokenSecond = responseCreateSecondUser.extract().path("accessToken");
        ValidatableResponse responseUpdateUser = userClient.changeUser(accessToken, ClientCredentials.from(userSecond));
        assertEquals(SC_FORBIDDEN, responseUpdateUser.extract().statusCode());
        assertEquals("User with such email already exists", responseUpdateUser.extract().path("message"));
        userClient.deleteUser(accessTokenSecond);
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
