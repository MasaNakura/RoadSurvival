# Road Survival

Road Survival is a racing game on Android. The goal of the player is to survive as long as they can without hitting an obstacle.

### Remarks
While Road Survival started as a group project in a computer science class I took in my junior year of high school, I coded the bulk of it the following summer. 
If you attempt to play Road Survival (instructions below), you will see how terrible my front-end development skills were (and still probably are).
The bulk of my learning experience was in the backend, such as setting up a database to store + display online rankings among registered players, allowing users to change car colors, and enabling multi-touch functionality for implementing the same-device multiplayer mode. 

### How to play
If you have an Android device (or emulator), you can try out my game [here](https://www.amazon.com/Road-Survival-Multiplayer-Racing-Game/dp/B09G5R78NC/ref=sr_1_5?crid=2YKLGKZM9AN8T&dib=eyJ2IjoiMSJ9.tKMKB6RnmR1Aw4pyueGnZJDWU86TuYS5BxbZ0CjSZH7a9zL4gIlndu_Ldt0iDYz50JPOtFIza9guew9JpXFu78cD_BkkNo9NZBBe9jx4xLZbJvKxAjC6PYHLJ2Qt0y6Dt6awp2DqbQ_hDynXmETON5XZl-YcdpfxJteHEv0PUf4zw88vtAvqmOGBJOG69VpRy2oEqs3FR-5ouPEpY3j3q2sIwZjvr4oijJ_Qv9J_O7P1M2A8HwN5wVgJpD9qsIvb7czFowtgL0q3UUjHl0ybTWK7wDYnP7FMQeDTHnXnvhE.mhJGNVLwvoYosq_FaXxalSck_t9yGgt0VS7tNj0jlLo&dib_tag=se&keywords=road+survival&qid=1710902353&s=mobile-apps&sprefix=road+survival%2Cmobile-apps%2C452&sr=1-5) on the Amazon Appstore!

## Some Features

### Online Leaderboard
When you click into the leaderboard, you will see the top 5 highest-scoring players and their scores! 
To implement the leaderboard, I used Google's Firebase to store all information about the player (Firebase has a free plan, is lightweight, and is easy to use!). 
When a user launches the app for the first time, they are prompted to register a unique username that will be shown in the leaderboard. 
I store a player's score in the database if they reach a new high score and update the leaderboard if the new score is high enough. 

### Multiplayer Mode
There is a multiplayer mode where 2 people can play simultaneously on the same device. This was inspired by some of the same-device multiplayer mobile games I played as a child.
They are always extremely difficult to play on tiny phone screens, but they're somehow really fun to play with someone else side by side. 
The goal of a user in this mode is to, again, survive as long as possible without hitting an obstacle; but a player now only has half of the screen to move around their car.
Don't worry, the number of obstacles each player gets is the same to keep it fair, but the placement is random so luck might be a big factor in winning (like any great game :')) 
The hardest part of implementing this part was enabling multiple fingers to touch the screen at the same time. 
You, like I, might think a multi-touch functionality should be extremely simple to implement, but it was a lot more complicated than I thought to allow different people to control different cars at the same time, so this was a great learning experience. 

### Music
I added a (free to use) music to this game. It is very intense, fun, noisy, and annoying. Perhaps its better to mute the game when playing:)
