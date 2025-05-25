package flappy_birds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import flappy_birds.Player;

public class LeaderboardManager {

    private List<Player> leaderboard = new ArrayList<>();

    public LeaderboardManager() {
        generateImaginaryPlayers();
    }

    private void generateImaginaryPlayers() {
        Random random = new Random();
        String[] names = {"Faker", "Palmer", "Hazard", "Son", "Morgan", "Top", "GDragon", "Bjergsen", "Ronaldo", "Messi"};
        for (int i = 0; i < 10; i++) {
            String name = names[i];
            int score = 3 + random.nextInt(100);
            leaderboard.add(new Player(name, score));
        }
        sortLeaderBoard();
    }

    private void sortLeaderBoard() {
        for (int i = 1; i < leaderboard.size(); i++) {
            Player current = leaderboard.get(i);
            int position = i - 1;

            // Move elements that are smaller than current to the right
            while (position >= 0 && leaderboard.get(position).getScore() < current.getScore()) {
                leaderboard.set(position + 1, leaderboard.get(position));
                position--;
            }
            leaderboard.set(position + 1, current);
        }
    }

    public void addPlayer(String name, int score) {
        leaderboard.add(new Player(name, score));
        sortLeaderBoard();
    }

    public List<Player> getLeaderboard() {
        return leaderboard;
    }

}
