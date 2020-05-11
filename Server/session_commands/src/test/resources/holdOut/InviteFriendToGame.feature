Feature: Users can invite friends to join game sessions
    As an authenticated user wanting to play a game with friends or family
    I want to be able to invite friends to the game session I'm currently on
    So that I can play a game of charades with my friends.

    Scenario: User invites friend to game session.
        Given that I am logged in,
        And I am in a game session,
        And I see a friend I want to invite at the friends list,
        When I click the invite button related to that friend
        Then I should be notified that a message with the invitation was sent to my friend.