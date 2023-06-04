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
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class TestCreateOrder {
    private User userSpecific;
    private UserClient userClient;
    private String accessToken;
    private String refreshToken;
    private OrderRequest orderRequest;
    final String USER_LOGIN = "/api/auth/login";
    final String API_ORDER = "/api/orders";

    @Before
    public void setUp() {
        userSpecific = UserGenerator.getSpecificUser();
        userClient = new UserClient();
        orderRequest = new OrderRequest();
    }

    @Test
    @DisplayName("Создание заказа для авторизованного пользователя")
    public void testOrderCanByCreatedIfUserAuthorized() {
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(userSpecific),USER_LOGIN);
        accessToken = loginResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");

        String bodyForTest = "{\n\"ingredients\": [\"61c0c5a71d1f82001bdaaa6f\", \"61c0c5a71d1f82001bdaaa7a\", \"61c0c5a71d1f82001bdaaa79\"]\n}";

        ValidatableResponse orderCreateResponse = orderRequest.createOrderWithToken(accessToken, bodyForTest, API_ORDER);

        int statusCode = orderCreateResponse.extract().statusCode();
        String actualBody = orderCreateResponse.extract().body().path("success").toString();
        String expectedBody = "true";

        assertEquals(SC_OK, statusCode);
        assertEquals(expectedBody, actualBody);

    }

    @Test
    @DisplayName("Создание заказа для неавторизованного пользователя")
    public void testOrderCanByCreatedIfUserNotAuthorized() {
        String bodyForTest = "{\n\"ingredients\": [\"61c0c5a71d1f82001bdaaa6f\", \"61c0c5a71d1f82001bdaaa7a\", \"61c0c5a71d1f82001bdaaa79\"]\n}";
        ValidatableResponse orderCreateResponse = orderRequest.createOrderWithoutToken(bodyForTest, API_ORDER);

        int statusCode = orderCreateResponse.extract().statusCode();
        String actualBody = orderCreateResponse.extract().body().path("success").toString();
        String expectedBody = "true";

        assertEquals(SC_OK, statusCode);
        assertEquals(expectedBody, actualBody);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testOrderCantByCreatedWithoutIngredients() {
        String bodyForTest = "{\n\"ingredients\": []\n}";
        ValidatableResponse orderCreateResponse = orderRequest.createOrderWithoutIngredients(bodyForTest, API_ORDER);

        int statusCode = orderCreateResponse.extract().statusCode();
        String actualBody = orderCreateResponse.extract().body().path("success").toString();
        String expectedBody = "false";

        assertEquals(SC_BAD_REQUEST, statusCode);
        assertEquals(expectedBody, actualBody);
    }

    @Test
    @DisplayName("Создание заказа с некорректными ингредиентами")
    public void testOrderCantByCreatedWithIncorrectIngredients() {
        String bodyForTest = "{\n\"ingredients\": [131]\n}";
        ValidatableResponse orderCreateResponse = orderRequest.createOrderWithIncorrectIngredients(bodyForTest, API_ORDER);

        int statusCode = orderCreateResponse.extract().statusCode();
        assertEquals(SC_INTERNAL_SERVER_ERROR, statusCode);
    }

    @After
    public void cleanUpUser() {
        if (accessToken != null) {
            userClient.logout(refreshToken, USER_LOGIN);
        }
    }

}
