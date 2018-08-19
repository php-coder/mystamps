*** Settings ***
Documentation    Verify category creation validation scenarios
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       category  validation

*** Test Cases ***
Create category with too short name
	Input Text              id=name  jj
	Input Text              id=nameRu  яя
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Value is less than allowable minimum of 3 characters
	Element Text Should Be  id=nameRu.errors  Value is less than allowable minimum of 3 characters

Create category with too long name
	${englishLetter}=       Set Variable  j
	${russianLetter}=       Set Variable  я
	Input Text              id=name  ${englishLetter * 51}
	Input Text              id=nameRu  ${russianLetter * 51}
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Value is greater than allowable maximum of 50 characters
	Element Text Should Be  id=nameRu.errors  Value is greater than allowable maximum of 50 characters

Create category with forbidden characters in name
	Input Text              id=name  S0m3+CategoryN_ame
	Input Text              id=nameRu  Категория_1+23
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Value must consist only latin letters, hyphen or spaces
	Element Text Should Be  id=nameRu.errors  Value must consist only Russian letters, hyphen or spaces

Create category with repeating hyphens in name
	Input Text              id=name  te--st
	Input Text              id=nameRu  те--ст
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Value must not contain repetition of hyphen
	Element Text Should Be  id=nameRu.errors  Value must not contain repetition of hyphen

Create category with name that starts with hyphen
	Input Text              id=name  -test
	Input Text              id=nameRu  -тест
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Value must not start or end with hyphen
	Element Text Should Be  id=nameRu.errors  Value must not start or end with hyphen

Create category with name that ends with hyphen
	Input Text              id=name  test-
	Input Text              id=nameRu  тест-
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Value must not start or end with hyphen
	Element Text Should Be  id=nameRu.errors  Value must not start or end with hyphen

Create category with forbidden names
	# Open a page again to have a clean state (nameRu field has an invalid value)
	Go To                   ${SITE_URL}/category/add
	# 'add' is a forbidden value
	Input Text              id=name  add
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Invalid value
	# 'list' is a forbidden value
	Input Text              id=name  list
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Invalid value

Create category with existing (non-unique) name
	Input Text              id=name  Sport
	Input Text              id=nameRu  Спорт
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Category already exists
	Element Text Should Be  id=nameRu.errors  Category already exists

Create category with existing name but in a different case
	Input Text              id=name  sport
	Input Text              id=nameRu  спорт
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Category already exists
	Element Text Should Be  id=nameRu.errors  Category already exists

Create category with non-existing name but existing (non-unique) slug
	Input Text              id=name  Prehistoric - animals
	# clear a value after a previous test to prevent its validation and looking up in database
	Clear Element Text      id=nameRu
	Submit Form             id=add-category-form
	Element Text Should Be  id=name.errors  Category with similar name already exists

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/category/add

After Test Suite
	Log Out
	Close Browser
