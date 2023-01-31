import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Locale;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class Steps {
    @Step("Initializing a new courier")
    public static CourierNew getValidDataForSuccessfullRegistration() {
        Faker fakerLocale = new Faker(new Locale("ru"));
        Faker faker = new Faker();
        String firstName=fakerLocale.name().firstName();
        String login=faker.name().username();
        String password=faker.name().firstName()+faker.code().asin();
        return new CourierNew(login, password, firstName);
    }

    @Step("Send a POST request for an endpoint \"/api/v1/courier\" to register a new courier")
    public static Response sendPostRequestSetUp(CourierNew json) {
        return given().header("Content-type", "application/json").and().body(json).post("/api/v1/courier");
    }

    @Step ("Send a POST request for an endpoint \"/api/v1/courier/login\" to log in")
    public static Response sendPostRequestLogin (CourierLogIn json){
        return given().header("Content-type","application/json").and().body(json).post("/api/v1/courier/login");
    }

    @Step("Check status code and text {\"ok\":true}in response body")
    public static void checkStatusCodeAndBody(Response response, int statusCode) {
        response.then().statusCode(statusCode).and().assertThat().body(equalTo("{\"ok\":true}"));
    }

    @Step("Check status code and message in response")
    public static void checkStatusCodeAndMessage(Response response, int statusCode, String message) {
        response.then().statusCode(statusCode).and().assertThat().body("message", equalTo(message));
    }

    @Step ("Check status code and existence of ID in response body")
    public static void checkStatusCodeAndId(Response response, int statusCode){
        response.then().statusCode(statusCode).and().assertThat().body("id",notNullValue());
    }

    //Deleting a courier after testing
    public static void deleteNewCourier(CourierNew json) {
        CourierLogIn loginJson = new CourierLogIn(json.getLogin(),json.getPassword());
        Response postResponse = sendPostRequestLogin(loginJson);
        if (postResponse.statusCode()==200) {
            int id = postResponse.then().extract().body().path("id");
            String idJson = "{\"id\" : " + id + "}";
            Response deleteResponse = given().header("Content-type", "application/json").and().body(idJson).delete("/api/v1/courier/" + id);
        }
    }
}
