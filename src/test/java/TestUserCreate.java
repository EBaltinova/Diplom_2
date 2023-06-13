import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserClient;
import user.UserCredentials;
import user.UserGenerator;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class TestUserCreate {

    private User user;
    private User userWithoutNecessaryField;
    private UserClient userClient;
    private String accessToken;
    final String USER_REGISTER = "/api/auth/register";
    final String USER_LOGIN = "/api/auth/login";
    final String USER_AUTHORIZATION = "/api/auth/user";


    @Before
    public void setUp() {
        user = UserGenerator.getDefault();
        userWithoutNecessaryField = UserGenerator.getUserWithoutNecessaryField();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Регистрация пользователя с корректными данными")
    public void testUserCanBeCreated() {
        ValidatableResponse response = userClient.create(user, USER_REGISTER);
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user), USER_LOGIN); // для логина, чтоб получить accessToken
        accessToken = loginResponse.extract().path("accessToken"); // Вытащить accessToken, что передать его в cleanUpUser, для удаления курьера после тестирования

        int statusCode = response.extract().statusCode();
        String actualBody = response.extract().body().path("success").toString();
        String expectedBody = "true";

        assertEquals(SC_OK, statusCode); //  Переиспользовать код статуса из интерфейса HttpStatus
        assertEquals(expectedBody, actualBody);
    }

    @Test
    @DisplayName("Повторная регистрация одного и того же пользователя")
    public void testPreviouslyCreatedUserCantBeCreatedAgain() {
        ValidatableResponse responseFirstUser = userClient.create(user, USER_REGISTER);
        ValidatableResponse responseSecondUser = userClient.create(user, USER_REGISTER);
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user), USER_LOGIN);
        accessToken = loginResponse.extract().path("accessToken");
        int statusCode = responseSecondUser.extract().statusCode();
        String actualBody = responseSecondUser.extract().body().path("success").toString();
        String expectedBody = "false";

        assertEquals(SC_FORBIDDEN, statusCode);
        assertEquals(expectedBody, actualBody);
    }

    @Test
    @DisplayName("Регистрация пользователя у которого отсутствует часть необходимых данных")
    public void testUserCantBeCreatedWithoutNecessaryField() {
        ValidatableResponse response = userClient.create(userWithoutNecessaryField, USER_REGISTER);
        int statusCode = response.extract().statusCode();
        String actualBody = response.extract().body().path("success").toString();
        String expectedBody = "false";

        assertEquals(SC_FORBIDDEN, statusCode);
        assertEquals(expectedBody, actualBody);
    }

    @After
    public void cleanUpUser() {
        if (accessToken != null) {
            userClient.delete(accessToken, USER_AUTHORIZATION);
        }
    }

}
