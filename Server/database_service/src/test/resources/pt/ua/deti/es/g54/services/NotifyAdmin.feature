Feature: During a session, any user can notify the admin by raising any hand over their head.
    As an authenticated user playing in a game session
    I want to be able to notify the platform admin
    So that he can clear some game rule or help in any problem.

    Scenario: User raises right hand.
        Given that I am logged in,
        And I am in a game session,
        When I raise my right hand above my head
        Then I should be notified that a message was send to the admin

    Scenario: User raises left hand.
        Given that I am logged in,
        And I am in a game session,
        When I raise my left hand above my head
        Then I should be notified that a message was send to the admin