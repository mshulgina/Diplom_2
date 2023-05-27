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
import java.util.ArrayList;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@DisplayName("Получение списка заказов")
public class GetOrdersWithUserTest {
    private User user;
    private Ingredients ingredients;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserInfo.getUser();
        ingredients = Order.getIngredients();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя.")
    @Description("Получение списка заказов авторизованного пользователя. Проверка успешного ответа от сервера.")
    public void getOrdersWithAuth() {
        int totalOrder = 1;
        userClient.createUser(ClientCredentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(ClientCredentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        orderClient.createOrderStep(ingredients, accessToken);
        ValidatableResponse responseGetOrders = orderClient.getOrdersForUserStep(accessToken);
        ArrayList<Object> orders = responseGetOrders.extract().path("orders");
        assertEquals(SC_OK, responseGetOrders.extract().statusCode());
        assertTrue(responseGetOrders.extract().path("success"));
        assertEquals(totalOrder, orders.size());
    }

    @Test
    @DisplayName("Получение списка заказов без авторизации.")
    @Description("Получение списка заказов без авторизации. Проверка неуспешного ответа от сервера.")
    public void getOrdersWithoutAuth() {
        userClient.createUser(ClientCredentials.from(user));
        ValidatableResponse responseLogin = userClient.loginUser(ClientCredentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        orderClient.createOrderStep(ingredients, accessToken);
        ValidatableResponse responseGetOrders = orderClient.getOrdersForUserStep("");
        assertEquals(SC_UNAUTHORIZED, responseGetOrders.extract().statusCode());
        assertFalse(responseGetOrders.extract().path("success"));
        assertEquals("You should be authorised", responseGetOrders.extract().path("message"));
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
