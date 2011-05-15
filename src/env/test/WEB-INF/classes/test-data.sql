-- see:
-- ru.mystamps.web.tests.page.AbstractPage.login()
-- ru.mystamps.web.tests.cases.WhenUserActivateAccount.loginShouldBeUnique()
-- ru.mystamps.web.tests.cases.WhenUserAuthenticates.validCredentialsShouldAuthenticateUserOnSite()
INSERT INTO users(login, name, email, registered_at, activated_at, hash, salt)
VALUES ('coder', 'Test Suite', 'coder@rock.home', NOW(), NOW(), '9a08f1a38b780eaa610e72c01db055ebee6e9d85', 'cEjinQJY3v');
