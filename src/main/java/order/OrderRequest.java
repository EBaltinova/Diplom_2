package order;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import util.Client;
import static io.restassured.RestAssured.given;

public class OrderRequest extends Client {
    @Step("Создание заказа с токеном авторизации")
    public ValidatableResponse createOrderWithToken(String accessToken,String bodyForTest, String API_ORDER) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(bodyForTest).log().all()
                .when()
                .post(API_ORDER)
                .then().log().all();
    }
    @Step("Создание заказа без токена авторизации")
    public ValidatableResponse createOrderWithoutToken(String bodyForTest, String API_ORDER) {
        return given()
                .spec(getSpec())
                .body(bodyForTest).log().all()
                .when()
                .post(API_ORDER)
                .then().log().all();
    }
    @Step("Создание заказа без ингредиентов")
    public ValidatableResponse createOrderWithoutIngredients(String bodyForTest, String API_ORDER) {
        return given()
                .spec(getSpec())
                .body(bodyForTest).log().all()
                .when()
                .post(API_ORDER)
                .then().log().all();
    }
    @Step("Создание заказа с некорректными ингредиентами")
    public ValidatableResponse createOrderWithIncorrectIngredients(String bodyForTest, String API_ORDER) {
        return given()
                .spec(getSpec())
                .body(bodyForTest).log().all()
                .when()
                .post(API_ORDER)
                .then().log().all();
    }
    @Step("Получение списка заказов пользователя с авторизацией")
    public static ValidatableResponse gettingUserOrdersList(String accessToken, String API_ORDER) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .when()
                .get(API_ORDER)
                .then().log().all();
    }
    @Step("Получение списка заказов пользователя без авторизации")
    public static ValidatableResponse gettingUserOrdersListWithoutLogin(String API_ORDER) {
        return given()
                .spec(getSpec())
                .when()
                .get(API_ORDER)
                .then().log().all();
    }
}
