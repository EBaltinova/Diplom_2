import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.*;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class TestUserChange {
    private User user;
    private User userWithAnotherData;
    private UserClient userClient;
    private String accessToken;
    final String USER_REGISTER = "/api/auth/register";
    final String USER_LOGIN = "/api/auth/login";
    final String USER_AUTHORIZATION = "/api/auth/user";

    @Before
    public void setUp() {
        user = UserGenerator.getDefault();
        userWithAnotherData = UserGenerator.getSpecificUserForChangeData();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Изменение данных авторизованного пользователя")
    public void testUserCanChangeDataIfTheyRegistered() {
        userClient.create(user, USER_REGISTER);
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user), USER_LOGIN);
        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse changeResponse = userClient.change(accessToken, UserCredentialsToChange.from(userWithAnotherData), USER_AUTHORIZATION);
        int statusCode = changeResponse.extract().statusCode();
        String actualBody = changeResponse.extract().body().path("success").toString();
        String expectedBody = "true";

        assertEquals(SC_OK, statusCode);
        assertEquals(expectedBody, actualBody);
    }

    @Test
    @DisplayName("Изменение данных неавторизованного пользователя")
    public void testUserCantChangeDataIfTheyNotRegistered() {
        accessToken = "123"; // Задан несуществующий токен, для имитации неавторизованного пользователя
        ValidatableResponse changeResponse = userClient.change(accessToken, UserCredentialsToChange.from(userWithAnotherData), USER_AUTHORIZATION);
        int statusCode = changeResponse.extract().statusCode();
        String actualBody = changeResponse.extract().body().path("success").toString();
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
