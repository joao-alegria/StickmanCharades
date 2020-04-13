Feature: Users can join a game session
    As an authenticated user wanting to play a game with friends or family
    I want to be able to join a game session on the platform
    So that I can play a game of charades with my friends.

    Scenario: Session creator joins its own session.
        Given that I am logged in,
        And I see the game session I created that I want to join in the game sessions list,
        When I click the join button of that game session
        Then I should see the game session lobby
        And I should be the admin. 

    Scenario: User joins session of another user.
        Given that I am logged in,
        And I see the game session I want in the game session list,
        When I click the join button of that session,
        Then I should see the game session lobby