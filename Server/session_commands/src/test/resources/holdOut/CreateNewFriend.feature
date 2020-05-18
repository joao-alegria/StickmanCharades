Feature: Users ask users to be their friends
    As an authenticated user
    I want to be able to ask other users to be my friends in the platform
    So that I in the future can invite them to game sessions.

    Scenario: User asserts that they can ask other users to be their friends.
        Given that I am logged in,
        When I click the invite user as friend button
        Then I should see a form that allows the user to insert another user's username.

    Scenario: User ask other users to be their friends.
        Given that I am logged in,
        And I'm in the invite user as friend form
        When I click the send notification button,
        Then I should be notified that a message with my friendship request was sent.

    Scenario: User gets notified that other user accepted their friendship request.
        Given that I am logged in,
        When I'm using the platform and other user accepts my friendship request,
        Then I should be notified that that user has accepted my friendship request.