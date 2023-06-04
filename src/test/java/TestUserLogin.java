import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
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

public class TestUserLogin {
    private User userSpecific;
    private User userSpecificWithoutRightAuthorisationData;
    private UserClient userClient;
    private String accessToken;
    private String refreshToken;
    final String USER_LOGIN = "/api/auth/login";

    @Before
    public void setUp() {
        userSpecific = UserGenerator.getSpecificUser();
        userSpecificWithoutRightAuthorisationData = UserGenerator.getSpecificUserWithNotRightLogin();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Авторизация существующего пользователя с корректными данными")
    public void testLoginWithRightAuthorisationDataSuccessful() {
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(userSpecific), USER_LOGIN);
        accessToken = loginResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");
        int statusCode = loginResponse.extract().statusCode();
        String actualBody = loginResponse.extract().body().path("success").toString();
        String expectedBody = "true";

        assertEquals(SC_OK, statusCode);
        assertEquals(expectedBody, actualBody);
    }

    @Test
    @DisplayName("Авторизация существующего пользователя с некорректным логином")
    public void testLoginWithoutRightAuthorisationDataNotSuccessful() {
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(userSpecificWithoutRightAuthorisationData), USER_LOGIN);
        int statusCode = loginResponse.extract().statusCode();
        String actualBody = loginResponse.extract().body().path("success").toString();
        String expectedBody = "false";

        assertEquals(SC_UNAUTHORIZED, statusCode);
        assertEquals(expectedBody, actualBody);
    }

    @After
    public void cleanUpUser() {
        if (accessToken != null) {
            userClient.logout(refreshToken, USER_LOGIN);
        }
    }
}