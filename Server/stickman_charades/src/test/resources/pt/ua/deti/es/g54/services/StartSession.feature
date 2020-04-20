Feature: When joining a session, any user should adopt the initial position.
    As an authenticated user joining a game session
    I want the camera to work to be able to perform the initial position
    So that the platform recognizes me and the game session can start.

    Scenario: User joins the session.
        Given that I am logged in,
        And I just joined a game session,
        When I perform the initial position(spread arms)
        Then I should be recognized by the platform
        And if am the last user recognized, the game session should start. 