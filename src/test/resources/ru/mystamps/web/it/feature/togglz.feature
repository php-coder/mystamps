Feature: Togglz console

  Scenario: Anonymous user opens Togglz console
      Given As anonymous user
       When I open Togglz console but I don't have enough permissions
       Then I see error message "Forbidden"
        And I see error code "403"

  Scenario: Authenticated user opens Togglz console
      Given As authenticated user
       When I open Togglz console but I don't have enough permissions
       Then I see error message "Forbidden"
        And I see error code "403"
