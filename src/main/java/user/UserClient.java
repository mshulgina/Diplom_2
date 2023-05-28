package user;

import baseUtil.ClientCredentials;
import baseUtil.ClientSettings;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends ClientSettings {
    private static final String PATH_CREATE = "/api/auth/register";
    private static final String PATH_LOGIN = "/api/auth/login";
    private static final String PATH_CHANGE_USER = "/api/auth/user";
    private static final String PATH_DELETE = "/api/auth/user";

    @Step("Создать пользователя")
    public ValidatableResponse createUser(ClientCredentials credentials) {
        return given()
                .spec(getSpec())
                .body(credentials)
                .when()
                .post(PATH_CREATE)
                .then();
    }

    @Step("Авторизовать пользователя")
    public ValidatableResponse loginUser(ClientCredentials credentials) {
        return given()
                .spec(getSpec())
                .body(credentials)
                .when()
                .post(PATH_LOGIN)
                .then();
    }

    @Step("Изменить данные пользователя")
    public ValidatableResponse changeUser(String accessToken, ClientCredentials credentials) {
        return given()
                .header("Authorization", accessToken)
                .header("Accept", "*/*")
                .spec(getSpec())
                .body(credentials)
                .when()
                .patch(PATH_CHANGE_USER)
                .then();
    }

    @Step("Удалить пользователя")
    public void deleteUser(String accessToken) {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .header("Accept", "*/*")
                    .spec(getSpecForDelete())
                    .delete(PATH_DELETE)
                    .then();
        }
    }
}
