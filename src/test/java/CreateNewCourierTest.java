import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateNewCourierTest {

    @Before
    public void setUp(){
        RestAssured.baseURI="https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Create courier with valid data and get status code 201")
    @Description("We register a courier with valid data and expect to receive a status code 201 and {ok: true} in the response body(positive test)")
    public void createCourierWithValidDataAndGetStatusCode201() {
        CourierNew json = getValidDataForSuccessfullRegistration();
        Response response = sendPostRequestSetUp(json);
        checkStatusCodeAndBody(response, 201);
        if (response.statusCode()==201) {
            deleteNewCourier(json);
        }
    }

    @Test
    @DisplayName("Create courier's twin and get unsuccessed status code 409")
    @Description("We register 2x couriers with completely identical data and expect to receive the status code 409 and correct message(Этот логин уже используется) when registering a second courier with the same data (negative test)")
    public void createCouriersTwinAndGetUnsuccessedStatusCode409() {
        CourierNew json = getValidDataForSuccessfullRegistration();
        Response response1 = sendPostRequestSetUp(json);
        Response response2 = sendPostRequestSetUp(json);
        checkStatusCodeAndMessage(response2, 409, "Этот логин уже используется");
        if (response1.statusCode()==201) {
            deleteNewCourier(json);
        }
        if (response2.statusCode()==201){
            deleteNewCourier(json);
        }
    }

    @Test
    @DisplayName("Create courier with existing login and get unsuccessed status code 409")
    @Description("We register 2x couriers with completely identical logins and expect to receive the status code 409 and correct message(Этот логин уже используется) when registering a second courier with the same data (negative test) ")
    public void createCourierWithExistingLoginAndGetUnsuccessedStatusCode409() {
        CourierNew json1 = getValidDataForSuccessfullRegistration();
        CourierNew json2 = getValidDataForSuccessfullRegistration();
        json2.setLogin(json1.getLogin());
        Response response1 = sendPostRequestSetUp(json1);
        Response response2 = sendPostRequestSetUp(json2);
        checkStatusCodeAndMessage(response2, 409, "Этот логин уже используется");
        if (response1.statusCode()==201) {
            deleteNewCourier(json1);
        }
        if (response2.statusCode()==201){
            deleteNewCourier(json2);
        }
    }

    @Test
    @DisplayName("Create courier without login and get unsuccessed status code 400")
    @Description("We register the courier without a login and expect that registration will not pass and we will receive the status code 400 and correct message(Недостаточно данных для создания учетной записи) (negative test)")
    public void createCourierWithoutLoginAndGetUnsuccessedStatusCode400(){
        CourierNew json= getValidDataForSuccessfullRegistration();
        json.setLogin(null);
        Response response= sendPostRequestSetUp(json);
        checkStatusCodeAndMessage(response, 400, "Недостаточно данных для создания учетной записи");
        if (response.statusCode()==201) {
            deleteNewCourier(json);
        }
    }

    @Test
    @DisplayName("Create courier without password and get unsuccessed status code 400")
    @Description("We register the courier without a password and expect that registration will not pass and we will receive the status code 400 and correct message(Недостаточно данных для создания учетной записи)(negative test)")
    public void createCourierWithoutPasswordAndGetUnsuccessedStatusCode400(){
        CourierNew json= getValidDataForSuccessfullRegistration();
        json.setPassword(null);
        Response response= sendPostRequestSetUp(json);
        checkStatusCodeAndMessage(response, 400, "Недостаточно данных для создания учетной записи");
        if (response.statusCode()==201) {
            deleteNewCourier(json);
        }
    }

    @Step ("1.Initializing a new courier")
    public CourierNew getValidDataForSuccessfullRegistration(){
        String[] namesArray= new String[]{"Lu","Mia","Jack","Emily","Thomas","Jessica","Isabella","Alexander"};
        String firstName =namesArray[new Random().nextInt(namesArray.length)];
        String login=String.format("%d%s%d",new Random().nextInt(100),firstName,new Random().nextInt(100));
        String password=firstName+(new Random().nextInt(100));
        return new CourierNew(login,password,firstName);
    }

    @Step ("2.Send a POST request for an endpoint \"/api/v1/courier\" to register a new courier")
    public Response sendPostRequestSetUp(CourierNew json){
        return given().header("Content-type","application/json").and().body(json).post("/api/v1/courier");
    }

    @Step ("3.Check status code and text {\"ok\":true}in response body")
    public void checkStatusCodeAndBody(Response response, int statusCode){
        response.then().statusCode(statusCode).and().assertThat().body(equalTo("{\"ok\":true}"));
    }

    @Step ("3.Check status code and message in response")
    public void checkStatusCodeAndMessage(Response response, int statusCode, String message){
        response.then().statusCode(statusCode).and().assertThat().body("message",equalTo(message));
    }

    @Step ("4.Deleting a courier after testing")
    public void deleteNewCourier(CourierNew json) {
        String loginJson="{\"login\":\""+json.getLogin()+"\",\"password\":\""+json.getPassword()+"\"}";
        Response response=given().header("Content-type","application/json").and().body(loginJson).post("/api/v1/courier/login");
        response.then().statusCode(200);
        int id=response.then().extract().body().path("id");
        String idJson="{\"id\" : "+id+"}";
        Response deleteResponse=given().header("Content-type","application/json").and().body(idJson).delete("/api/v1/courier/"+id);
        deleteResponse.then().statusCode(200);
    }
}
