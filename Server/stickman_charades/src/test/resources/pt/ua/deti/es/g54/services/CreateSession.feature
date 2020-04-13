Feature: Users can create a new game session
    As an authenticated user wanting to play a game with friends or family
    I want to be able to create a new game session on the platform
    So that I can invite friends or have random people join and play with me.

    Scenario: User asserts that it's possible to create a session.
        Given that I am logged in,
        And I have access to home lobby page,
        When I choose the option to create a game session
        Then I should see a form to be filled.

    Scenario: User creates a game session, both correctly and without necessary fields.
        Given that I am logged in,
        When I execute the previous steps
        And I fill in and submit the form,
        Then I should see a message informing me about the success/failure of the operation
        And (if successful) I should be redirected to the session lobby.