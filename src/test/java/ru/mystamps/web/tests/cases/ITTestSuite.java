package ru.mystamps.web.tests.cases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	/*
	WhenUserAtIndexPage.class,
	WhenUserRegisterAccount.class,
	WhenAuthenticatedUserRegisterAccount.class,
	WhenUserActivateAccount.class,
	WhenUserAuthenticates.class,
	WhenAuthenticatedUserTryToAuthenticates.class,
	WhenUserLogsOut.class,
	WhenUserAddStamps.class,
	WhenUserAddCountry.class,
	WhenUserRecoveryPassword.class,*/
	WhenUserOpenNotExistingPage.class/*,
	WhenSiteOnMaintenance.class*/
})
public class ITTestSuite {
}

