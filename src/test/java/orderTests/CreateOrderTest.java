package orderTests;

import baseUtil.ClientCredentials;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.Ingredients;
import order.Order;
import order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserClient;
import user.UserInfo;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DisplayName("Создание заказа")
public class CreateOrderTest {

    private User user;
    private Ingredients ingredients;
    private Ingredients ingredientsEmpty;
    private Ingredients ingredientsIncorrect;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserInfo.getUser();
        ingredients = Order.getIngredients();
        ingredientsEmpty = Order.getIngredientsEmpty();
        ingredientsIncorrect = Order.getIngredientsIncorrect();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией.")
    @Description("Создание заказа с авторизацией. Проверка успешного ответа от сервера.")
    public void createOrderWithAuthTest() {
        userClient.createUser(ClientCredentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(ClientCredentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = orderClient.createOrderStep(ingredients, accessToken);
        assertEquals(SC_OK, responseOrderCreate.extract().statusCode());
        assertTrue(responseOrderCreate.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации.")
    @Description("Создание заказа без авторизации. Проверка успешного ответа от сервера.")
    public void createOrderWithoutAuthTest() {
        ValidatableResponse responseUserCreate = userClient.createUser(ClientCredentials.from(user));
        accessToken = responseUserCreate.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = orderClient.createOrderWithoutAuthorizationStep(ingredients);
        assertEquals(SC_OK, responseOrderCreate.extract().statusCode());
        assertTrue(responseOrderCreate.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Создание заказа без ингредиентов. Проверка неуспешного ответа от сервера.")
    public void createOrderWithoutIngredientsTest() {
        ValidatableResponse responseUserCreate = userClient.createUser(ClientCredentials.from(user));
        accessToken = responseUserCreate.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = orderClient.createOrderWithoutAuthorizationStep(ingredientsEmpty);
        assertEquals(SC_BAD_REQUEST, responseOrderCreate.extract().statusCode());
        assertEquals("Ingredient ids must be provided", responseOrderCreate.extract().path("message"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хэшем ингредиентов.")
    @Description("Создание заказа с неверным хэшем ингредиентов. Проверка ошибки сервера.")
    public void createOrderWithIncorrectHashTest() {
        ValidatableResponse responseUserCreate = userClient.createUser(ClientCredentials.from(user));
        accessToken = responseUserCreate.extract().path("accessToken");
        ValidatableResponse responseOrderCreate = orderClient.createOrderWithoutAuthorizationStep(ingredientsIncorrect);
        assertEquals(SC_INTERNAL_SERVER_ERROR, responseOrderCreate.extract().statusCode());
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
