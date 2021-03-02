# Stickman Charades - An Online Privacy-Concerned Depth-Sensor-Based Game Platform 

![homepage](https://github.com/FilipePires98/StickmanCharades/blob/master/Documentation/HomePage-Screenshot.png)

## About Stickman Charades

Group activities such as the famous Trivial Pursuit and other games not requiring boards or cards are a very common way of spending quality time with close family and friends. 
These activities build rich traditions and valuable memories and stimulate our brains to cooperate and compete in a healthy way. 
However, they lack in the ability to bring closer people who are physically far from each other.

The concept behind our project is simple: the development of a prototype capable of bridging this gap by allowing multiple players to interact with each other at a distance in the game that we intend to offer – the Charades. 
The rules of Charades are: one person must choose an idea to act out (this can be something like a book or a movie, or anything else); this person must then try to act out the idea for the group so that they successfully guess what the person was trying to act out; the person who gets it right first gets to have the next turn acting something out.
To do this, we provide an online system capable of streaming the individual acts of one player to all the others within a given session. 
But there is a twist. 

In order to “gamify” the concept, instead of simply streaming the live video captured by the players’ devices, we will stream a customizable avatar of themselves with the help of Orbbec (a Kinect-like device) that will replicate all the moves made by users. 
This way, players will not only be able to play with their dear friends but will also practice with other players while maintaining their anonymity.
Also, by analysing the movements on the server, we are able to detect special combinations that trigger actions like 'Pause Game' and others.

## Technical Description

This repository contains the work done by the authors on the group project for the course in Software Engineering of the MSc. in Informatics Engineering of the University of Aveiro.
The assignment is focused on all the main concepts and technological solutions addressed in the course: 
- Design, implement and deploy an IT system using current DevOps best practices
- Use and apply Quality Assurance procedures through tests
- Work as a team of software developers

The deployment is done using Docker containers, automated with the help of Jenkins.
The solution is built using Springboot and uses technologies such as: PostgreSQL (for persistence), Kafka (for streaming and logging), JUnit and Cucumber (for testing), Unity Engine (for desktop UI) and AngularJS (for web UI).

## Repository Structure:

/Angular        - online platform, where sign-up and game download occurs

/Documentation  - system manuals

/Server         - back-end logic

/Unity          - Windows/Linux desktop application to play Stickman Charades, supporting Orbbec depth-sensor

## Authors

André Pedrosa

Filipe Pires

João Alegria

For further information, please read our [user manual](https://github.com/FilipePires98/StickmanCharades/blob/master/Documentation/Stickman%20Charades%20-%20User%20Manual.pdf) or contact us directly.