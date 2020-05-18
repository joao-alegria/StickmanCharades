Feature: During a session, any user can request an immediate session stop by making a signal.
    As an authenticated user in a game session
    I want to make the stop signal(cross arms over head)
    So that session is immediately closed.

    Scenario: User requests immediate session termination.
        Given that I am logged in,
        And I am in a game session,
        When I perform the stopping position(cross arms over head)
        Then I should see the game session to be immediately stopped. 