import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.OrderRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserClient;
import user.UserCredentials;
import user.UserGenerator;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class TestGettingOrdersList {

    private User user;
    private UserClient userClient;
    private String accessToken;
    final String USER_REGISTER = "/api/auth/register";
    final String USER_LOGIN = "/api/auth/login";
    final String USER_AUTHORIZATION = "/api/auth/user";
    final String API_ORDER = "/api/orders";

    @Before
    public void setUp() {
        user = UserGenerator.getDefault();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя")
    public void testOrderListCanBeGettingIfUserLogin() {
        ValidatableResponse response = userClient.create(user, USER_REGISTER);
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user), USER_LOGIN);
        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse gettingOrdersListResponse = OrderRequest.gettingUserOrdersList(accessToken, API_ORDER);

        int statusCode = gettingOrdersListResponse.extract().statusCode();
        String actualBody = gettingOrdersListResponse.extract().body().path("success").toString();
        String expectedBody = "true";

        assertEquals(SC_OK, statusCode);
        assertEquals(expectedBody, actualBody);
    }

    @Test
    @DisplayName("Получение списка заказов неавторизованного пользователя")
    public void testOrderListCantBeGettingIfUserNotLogin() {

        ValidatableResponse gettingOrdersListResponse = OrderRequest.gettingUserOrdersListWithoutLogin(API_ORDER);

        int statusCode = gettingOrdersListResponse.extract().statusCode();
        String actualBody = gettingOrdersListResponse.extract().body().path("success").toString();
        String expectedBody = "false";

        assertEquals(SC_UNAUTHORIZED, statusCode);
        assertEquals(expectedBody, actualBody);
    }

    @After
    public void cleanUpUser() {
        if (accessToken != null) {
            userClient.delete(accessToken, USER_AUTHORIZATION);
        }
    }

}
