package ru.netology.javaqa.test;

import org.junit.jupiter.api.*;
import ru.netology.javaqa.data.DataHelper;
import ru.netology.javaqa.data.SQLHelper;
import ru.netology.javaqa.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.javaqa.data.SQLHelper.cleanAuthCodes;
import static ru.netology.javaqa.data.SQLHelper.cleanDatabase;

public class BankLoginTest {
    LoginPage loginPage;

    @AfterEach
    void tearDown() {

        cleanAuthCodes();
    }

    @AfterAll
    static void tearDownAll() {

        cleanDatabase();
    }

    @BeforeEach
    void setUp() {

        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @Test

    public void shouldSuccessfulLogin() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test

    public void shouldWithRandomUserIsNotExistInDatabase() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
    }

    @Test

    public void shouldWithExistUserAndRandomVerificationCode() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
    }

    @Test

    public void shouldBlockedUsersWithExistLoginRandomPasswordThreeTimes() {
        var login = DataHelper.getAuthInfoWithTestData().getLogin();
        var password = DataHelper.generateRandomUser().getPassword();
        var authInfo = new DataHelper.AuthInfo(login, password);
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
        loginPage.clean();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
        loginPage.clean();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! Пользователь заблокирован");
    }
}