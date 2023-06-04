package user;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import util.Client;

import static io.restassured.RestAssured.given;

public class UserClient extends Client {

    @Step("Регистрация пользователя")
    public ValidatableResponse create(User user, String userRegister) {

        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(userRegister)
                .then().log().all();

    }
    @Step("Авторизация пользователя")
    public ValidatableResponse login(UserCredentials userCredentials, String USER_LOGIN) {

        return given()
                .spec(getSpec())
                .body(userCredentials)
                .when()
                .post(USER_LOGIN)
                .then().log().all();
    }
    @Step("Выход из личного кабинета")
    public ValidatableResponse logout(String accessToken, String USER_LOGIN) {
        return given()
                .spec(getSpec())
                .body(accessToken)
                .when()
                .post(USER_LOGIN)
                .then().log().all();
    }
    @Step("Изменение данных пользователя")
    public ValidatableResponse change(String accessToken, UserCredentialsToChange userCredentialsToChange, String USER_AUTHORIZATION) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(userCredentialsToChange)
                .when()
                .patch(USER_AUTHORIZATION)
                .then().log().all();
    }
    @Step("Удаление пользователя")
    public ValidatableResponse delete(String accessToken, String USER_AUTHORIZATION) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(USER_AUTHORIZATION)
                .then().log().all();
    }
}


