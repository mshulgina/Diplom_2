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

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;

@DisplayName("Создание пользователя")
public class CreateUserTest {
    private User user;
    private UserClient userClient;
    private String accessToken;
    private String accessTokenError;

    @Before
    public void setUp() {
        user = UserInfo.getUser();
        userClient = new UserClient();
        accessTokenError = null;
    }

    @Test
    @DisplayName("Создание уникального пользователя.")
    @Description("Создание уникального пользователя со случайным набором данных. Проверка успешного ответа сервера.")
    public void userCreatedTest() {
        ValidatableResponse responseCreate = userClient.createUser(ClientCredentials.from(user));
        accessToken = responseCreate.extract().path("accessToken");
        assertEquals(SC_OK, responseCreate.extract().statusCode());
    }

    @Test
    @DisplayName("Создание уже созданного пользователя.")
    @Description("Создание уже созданного пользователя со случайным набором данных. Проверка неуспешного ответа сервера.")
    public void userCantBeCreatedTwiceTest() {
        ValidatableResponse responseCreateUniqueUser = userClient.createUser(ClientCredentials.from(user));
        ValidatableResponse responseCreateExistUser = userClient.createUser(ClientCredentials.from(user));
        accessToken = responseCreateUniqueUser.extract().path("accessToken");
        if (responseCreateExistUser.extract().path("accessToken") != null) {
            accessTokenError = responseCreateExistUser.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreateExistUser.extract().statusCode());
        assertEquals("User already exists", responseCreateExistUser.extract().path("message"));
    }

    @Test
    @DisplayName("Создание пользователя без логина и пароля")
    @Description("Создание пользователя без логина и пароля. Проверка неуспешного ответа сервера.")
    public void userCantBeCreatedWithoutNameAndPasswordTest() {
        ValidatableResponse responseCreate = userClient.createUser(ClientCredentials.fromOnlyEmail(user));
        if (responseCreate.extract().path("accessToken") != null) {
            accessTokenError = responseCreate.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreate.extract().statusCode());
        assertEquals("Email, password and name are required fields", responseCreate.extract().path("message"));
    }

    @Test
    @DisplayName("Создание пользователя без логина и email")
    @Description("Создание пользователя без логина и email. Проверка неуспешного ответа сервера.")
    public void userCantBeCreatedWithoutNameAndEmailTest() {
        ValidatableResponse responseCreate = userClient.createUser(ClientCredentials.fromOnlyPassword(user));
        if (responseCreate.extract().path("accessToken") != null) {
            accessTokenError = responseCreate.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreate.extract().statusCode());
        assertEquals("Email, password and name are required fields", responseCreate.extract().path("message"));
    }

    @Test
    @DisplayName("Создание пользователя без логина")
    @Description("Создание пользователя без логина. Проверка неуспешного ответа сервера.")
    public void userCantBeCreatedWithoutNameTest() {
        ValidatableResponse responseCreate = userClient.createUser(ClientCredentials.fromOnlyEmailAndPassword(user));
        if (responseCreate.extract().path("accessToken") != null) {
            accessTokenError = responseCreate.extract().path("accessToken");
        }
        assertEquals(SC_FORBIDDEN, responseCreate.extract().statusCode());
        assertEquals("Email, password and name are required fields", responseCreate.extract().path("message"));
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
        if (accessTokenError != null) {
            userClient.deleteUser(accessTokenError);
        }
    }
}
