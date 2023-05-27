package order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import baseUtil.ClientSettings;

import static io.restassured.RestAssured.given;

public class OrderClient extends ClientSettings {
    private static final String PATH_CREATE = "/api/orders";
    private static final String PATH_GET_ORDERS = "/api/orders";

    @Step("Создать заказ с авторизацией")
    public ValidatableResponse createOrderStep(Ingredients ingredients, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .header("Accept", "*/*")
                .spec(getSpec())
                .body(ingredients)
                .when()
                .post(PATH_CREATE)
                .then();
    }

    @Step("Создать заказ без авторизации")
    public ValidatableResponse createOrderWithoutAuthorizationStep(Ingredients ingredients) {
        return given()
                .spec(getSpec())
                .body(ingredients)
                .when()
                .post(PATH_CREATE)
                .then();
    }

    @Step("Получить заказ конкретного пользователя с авторизацией")
    public ValidatableResponse getOrdersForUserStep(String accessToken) {
        if (accessToken != null) {
            return given()
                    .header("Authorization", accessToken)
                    .header("Accept", "*/*")
                    .spec(getSpecForGetOrders())
                    .get(PATH_GET_ORDERS)
                    .then();
        }
        return null;
    }
}