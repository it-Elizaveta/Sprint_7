import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LogInCourierTest {
    CourierNew jsonSetUp;

    @Before
    public void setUp(){
        RestAssured.baseURI=BaseURI.BASE_URI;
    }

    @Test
    @DisplayName("Authorize existing courier and get status code 200 and ID")
    @Description("We are logging in with the credentials of an existing courier and expect to receive a 200 response status code and ID in the response body (positive test)")
    public void authorizeExistingCourierAndGetStatusCode200AndId(){
        jsonSetUp= Steps.getValidDataForSuccessfullRegistration();
        Steps.sendPostRequestSetUp(jsonSetUp);
        CourierLogIn jsonLogIn=new CourierLogIn(jsonSetUp.getLogin(),jsonSetUp.getPassword());
        Response responseLogIn=Steps.sendPostRequestLogin(jsonLogIn);
        Steps.checkStatusCodeAndId(responseLogIn, 200);
    }

    @Test
    @DisplayName("Authorize not existing courier and get status code 404")
    @Description("We are logging in with the credentials of a non-existent courier and expect to receive the status response code 404 and correct message (Учетная запись не найдена) (negative test)")
        public void authorizeNotExistingCourierAndGetStatusCode404(){
        CourierLogIn jsonLogIn=new CourierLogIn("NotExistingUser","NotExistingPassword");
        Response responseLogIn=Steps.sendPostRequestLogin(jsonLogIn);
        Steps.checkStatusCodeAndMessage(responseLogIn, 404, "Учетная запись не найдена");
    }

    @Test
    @DisplayName("Authorize with incorrect login and get status code 404")
    @Description("We are logging in with incorrect login and expect to receive the status response code 404 and correct message (Учетная запись не найдена) (negative test)")
    public void authorizeWithIncorrectLoginAndGetStatusCode404(){
        jsonSetUp= Steps.getValidDataForSuccessfullRegistration();
        Steps.sendPostRequestSetUp(jsonSetUp);
        CourierLogIn jsonLogIn=new CourierLogIn("IncorrectLogin",jsonSetUp.getPassword());
        Response responseLogIn=Steps.sendPostRequestLogin(jsonLogIn);
        Steps.checkStatusCodeAndMessage(responseLogIn, 404, "Учетная запись не найдена");
    }

    @Test
    @DisplayName("Authorize with incorrect password and get status code 404")
    @Description("We are logging in with incorrect password and expect to receive the status response code 404 and correct message (Учетная запись не найдена) (negative test)")
    public void authorizeWithIncorrectPasswordAndGetStatusCode404(){
        jsonSetUp= Steps.getValidDataForSuccessfullRegistration();
        Steps.sendPostRequestSetUp(jsonSetUp);
        CourierLogIn jsonLogIn=new CourierLogIn(jsonSetUp.getLogin(),"IncorrectPassword");
        Response responseLogIn=Steps.sendPostRequestLogin(jsonLogIn);
        Steps.checkStatusCodeAndMessage(responseLogIn, 404, "Учетная запись не найдена");
    }

    @Test
    @DisplayName("Authorize without login and get status code 400")
    @Description("We are logging in without login and expect to receive the status response code 400 and correct message (Недостаточно данных для входа)(negative test)")
    public void authorizeWithoutLoginAndGetStatusCode400(){
        jsonSetUp= Steps.getValidDataForSuccessfullRegistration();
        Steps.sendPostRequestSetUp(jsonSetUp);
        CourierLogIn jsonLogIn=new CourierLogIn(null,jsonSetUp.getPassword());
        Response responseLogIn=Steps.sendPostRequestLogin(jsonLogIn);
        Steps.checkStatusCodeAndMessage(responseLogIn, 400, "Недостаточно данных для входа");
    }

    @Test
    @DisplayName("Authorize without password and get status code 400")
    @Description("We are logging in without password and expect to receive the status response code 400 and correct message (Недостаточно данных для входа) (negative test)")
    public void authorizeWithoutPasswordAndGetStatusCode400(){
        jsonSetUp= Steps.getValidDataForSuccessfullRegistration();
        Steps.sendPostRequestSetUp(jsonSetUp);
        CourierLogIn jsonLogIn=new CourierLogIn(jsonSetUp.getLogin(),"");
        Response responseLogIn=Steps.sendPostRequestLogin(jsonLogIn);
        Steps.checkStatusCodeAndMessage(responseLogIn, 400, "Недостаточно данных для входа");
    }

    @After
    public void tearDown() {

        try {
            Steps.deleteNewCourier(jsonSetUp);
        } catch (NullPointerException exeption) {
        }
    }
}
