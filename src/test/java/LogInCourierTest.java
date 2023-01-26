import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class LogInCourierTest {

    @Before
    public void setUp(){
        RestAssured.baseURI="http://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Authorize existing courier and get status code 200 and ID")
    @Description("We are logging in with the credentials of an existing courier and expect to receive a 200 response status code and ID in the response body (positive test)")
    public void authorizeExistingCourierAndGetStatusCode200AndId(){
        CourierNew jsonSetUp=getValidDataForSuccessfullRegistration();
        Response responseSetUp=sendPostRequestSetUp(jsonSetUp);
        CourierLogIn jsonLogIn=new CourierLogIn(jsonSetUp.getLogin(),jsonSetUp.getPassword());
        Response responseLogIn=sendPostRequestLogin(jsonLogIn);
        checkStatusCodeAndId(responseLogIn, 200);
        if (responseSetUp.statusCode()==201) {
            deleteNewCourierByPOJO(jsonSetUp);
        }
    }

    @Test
    @DisplayName("Authorize not existing courier and get status code 404")
    @Description("We are logging in with the credentials of a non-existent courier and expect to receive the status response code 404 and correct message (Учетная запись не найдена) (negative test)")
        public void authorizeNotExistingCourierAndGetStatusCode404(){
        CourierLogIn jsonLogIn=new CourierLogIn("NotExistingUser","NotExistingPassword");
        Response responseLogIn=sendPostRequestLogin(jsonLogIn);
        checkStatusCodeAndMessage(responseLogIn, 404, "Учетная запись не найдена");
        if (responseLogIn.statusCode()==200) {
            deleteNewCourierByResponse(responseLogIn);
        }
    }

    @Test
    @DisplayName("Authorize with incorrect login and get status code 404")
    @Description("We are logging in with incorrect login and expect to receive the status response code 404 and correct message (Учетная запись не найдена) (negative test)")
    public void authorizeWithIncorrectLoginAndGetStatusCode404(){
        CourierNew jsonSetUp=getValidDataForSuccessfullRegistration();
        Response responseSetUp=sendPostRequestSetUp(jsonSetUp);
        CourierLogIn jsonLogIn=new CourierLogIn("IncorrectLogin",jsonSetUp.getPassword());
        Response responseLogIn=sendPostRequestLogin(jsonLogIn);
        checkStatusCodeAndMessage(responseLogIn, 404, "Учетная запись не найдена");
        if (responseSetUp.statusCode()==201) {
            deleteNewCourierByPOJO(jsonSetUp);
        }
    }

    @Test
    @DisplayName("Authorize with incorrect password and get status code 404")
    @Description("We are logging in with incorrect password and expect to receive the status response code 404 and correct message (Учетная запись не найдена) (negative test)")
    public void authorizeWithIncorrectPasswordAndGetStatusCode404(){
        CourierNew jsonSetUp=getValidDataForSuccessfullRegistration();
        Response responseSetUp=sendPostRequestSetUp(jsonSetUp);
        CourierLogIn jsonLogIn=new CourierLogIn(jsonSetUp.getLogin(),"IncorrectPassword");
        Response responseLogIn=sendPostRequestLogin(jsonLogIn);
        checkStatusCodeAndMessage(responseLogIn, 404, "Учетная запись не найдена");
        if (responseSetUp.statusCode()==201) {
            deleteNewCourierByPOJO(jsonSetUp);
        }
    }

    @Test
    @DisplayName("Authorize without login and get status code 400")
    @Description("We are logging in without login and expect to receive the status response code 400 and correct message (Недостаточно данных для входа)(negative test)")
    public void authorizeWithoutLoginAndGetStatusCode400(){
        CourierNew jsonSetUp=getValidDataForSuccessfullRegistration();
        Response responseSetUp=sendPostRequestSetUp(jsonSetUp);
        CourierLogIn jsonLogIn=new CourierLogIn(null,jsonSetUp.getPassword());
        Response responseLogIn=sendPostRequestLogin(jsonLogIn);
        checkStatusCodeAndMessage(responseLogIn, 400, "Недостаточно данных для входа");
        if (responseSetUp.statusCode()==201) {
            deleteNewCourierByPOJO(jsonSetUp);
        }
    }

    @Test
    @DisplayName("Authorize without password and get status code 400")
    @Description("We are logging in without password and expect to receive the status response code 400 and correct message (Недостаточно данных для входа) (negative test)")
    public void authorizeWithoutPasswordAndGetStatusCode400(){
        CourierNew jsonSetUp=getValidDataForSuccessfullRegistration();
        Response responseSetUp=sendPostRequestSetUp(jsonSetUp);
        CourierLogIn jsonLogIn=new CourierLogIn(jsonSetUp.getLogin(),"");
        Response responseLogIn=sendPostRequestLogin(jsonLogIn);
        checkStatusCodeAndMessage(responseLogIn, 400, "Недостаточно данных для входа");
        if (responseSetUp.statusCode()==201) {
            deleteNewCourierByPOJO(jsonSetUp);
        }
    }

    @Step ("Initializing a new courier")
    public CourierNew getValidDataForSuccessfullRegistration(){
        String[] namesArray= new String[]{"Ян","Мия","Анна","Ольга","Андрей","Кристина","Александр","Анна-Мария"};
        String firstName =namesArray[new Random().nextInt(namesArray.length)];
        String login=String.format("%d%s%d",new Random().nextInt(100),firstName,new Random().nextInt(100));
        String password=firstName+(new Random().nextInt(100));
        return new CourierNew(login,password,firstName);
    }

    @Step ("Send a POST request for an endpoint \"/api/v1/courier\" to register a new courier")
    public Response sendPostRequestSetUp(CourierNew json){
        return given().header("Content-type","application/json").and().body(json).post("/api/v1/courier");
    }

    @Step ("Send a POST request for an endpoint \"/api/v1/courier/login\" to log in")
    public Response sendPostRequestLogin (CourierLogIn json){
        return given().header("Content-type","application/json").and().body(json).post("/api/v1/courier/login");
    }

    @Step ("Check status code and message in response")
    public void checkStatusCodeAndMessage(Response response, int statusCode, String message){
        response.then().statusCode(statusCode).and().assertThat().body("message",equalTo(message));
    }

    @Step ("Check status code and existence of ID in response body")
    public void checkStatusCodeAndId(Response response, int statusCode){
        response.then().statusCode(statusCode).and().assertThat().body("id",notNullValue());
    }

    @Step ("Deleting a courier after testing")
    public void deleteNewCourierByResponse(Response response) {
        int id=response.then().extract().body().path("id");
        System.out.println("id="+id);
        String idJson="{\"id\" : "+id+"}";
        Response deleteResponse=given().header("Content-type","application/json").and().body(idJson).delete("/api/v1/courier/"+id);
        deleteResponse.then().statusCode(200);
    }

    @Step ("Deleting a courier after testing")
    public void deleteNewCourierByPOJO(CourierNew json) {
        String loginJson="{\"login\":\""+json.getLogin()+"\",\"password\":\""+json.getPassword()+"\"}";
        Response response=given().header("Content-type","application/json").and().body(loginJson).post("/api/v1/courier/login");
        response.then().statusCode(200);
        int id=response.then().extract().body().path("id");
        String idJson="{\"id\" : "+id+"}";
        Response deleteResponse=given().header("Content-type","application/json").and().body(idJson).delete("/api/v1/courier/"+id);
        deleteResponse.then().statusCode(200);
    }
}
