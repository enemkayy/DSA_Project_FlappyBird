package flappy_birds;

import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.font.GlyphVector;
import java.util.List;

import pkg2dgamesframework.AFrameOnImage;
import pkg2dgamesframework.Animation;
import pkg2dgamesframework.GameScreen;

import javax.imageio.ImageIO;
import javax.swing.*;

import flappy_birds.Player;


public class FlappyBirds extends GameScreen {

    private BufferedImage titleImage;
    private BufferedImage bird_icon;
    private BufferedImage leaderboardTitle;

    private BufferedImage backgroundDay;

    private BufferedImage playButton;
    private BufferedImage exitButton;
    private BufferedImage pauseButton;
    private BufferedImage resumeButton;
    private BufferedImage restartButton;
    private BufferedImage leaderboardButton;
    private BufferedImage menuButton;

    private BufferedImage promptToPlay;
    private BufferedImage getReady;
    private BufferedImage instructions;

    private BufferedImage birds;
    private BufferedImage gameOver;
    private BufferedImage panelScore;

    private BufferedImage bronzeMedal;
    private BufferedImage silverMedal;
    private BufferedImage goldMedal;
    private BufferedImage platinumMedal;

    private Animation bird_anim;


    public static float g = 0.16f;

    private Bird bird;
    private Ground ground;
    private ChimneyGroup chimneyGroup;


    private int Point = 0;
    private int bestScore = 0;

    private final int MENU_SCREEN = 0;
    private final int BEGIN_SCREEN = 1;
    private final int GAMEPLAY_SCREEN = 2;
    private final int GAMEOVER_SCREEN = 3;
    private final int LEADERBOARD_SCREEN = 4;

    private int currentScreen = MENU_SCREEN;

    private boolean isPaused = false;

    private LeaderboardManager leaderboardManager = new LeaderboardManager();

    private int scrollOffset = 0;
    private final int ENTRY_HEIGHT = 30;
    private final int VISIBLE_ENTRIES = 9;

    // Scrollbar properties
    private final int SCROLLBAR_WIDTH = 15;
    private final int LEADERBOARD_X = 200;
    private final int LEADERBOARD_Y = 120;
    private final int LEADERBOARD_WIDTH = 400;
    private final int LEADERBOARD_HEIGHT = VISIBLE_ENTRIES * ENTRY_HEIGHT;
    private boolean isDraggingScrollbar = false;
    private int scrollbarDragOffset = 0;


    public FlappyBirds() throws IOException {

        super(800, 600);

        try {
            titleImage = ImageIO.read(new File("Assets/label_flappy_bird.png"));
            bird_icon = ImageIO.read(new File("Assets/bird_orange_1.png"));
            leaderboardTitle = ImageIO.read(
                    new File("Assets/leaderboard_title.png"));
            backgroundDay = ImageIO.read(
                    new File("Assets/background-day.png"));
            exitButton = ImageIO.read(
                    new File("Assets/exit_btn.png"));
            playButton = ImageIO.read(
                    new File("Assets/button_play_normal.png"));
            pauseButton = ImageIO.read(
                    new File("Assets/button_pause.png"));
            resumeButton = ImageIO.read(
                    new File("Assets/button_resume.png"));
            promptToPlay = ImageIO.read(
                    new File("Assets/prompt.png"));
            getReady = ImageIO.read(
                    new File("Assets/label_get_ready.png"));
            instructions = ImageIO.read(
                    new File("Assets/instructions.png"));
            gameOver = ImageIO.read(
                    new File("Assets/gameover.png"));
            panelScore = ImageIO.read(
                    new File("Assets/panel_score.png"));
            restartButton = ImageIO.read(
                    new File("Assets/button_restart.png"));
            leaderboardButton = ImageIO.read(
                    new File("Assets/button_score_normal.png"));
            menuButton = ImageIO.read(
                    new File("Assets/button_menu.png"));
            bronzeMedal = ImageIO.read(
                    new File("Assets/medal_bronze.png"));
            silverMedal = ImageIO.read(
                    new File("Assets/medal_silver.png"));
            goldMedal = ImageIO.read(
                    new File("Assets/medal_gold.png"));
            platinumMedal = ImageIO.read(
                    new File("Assets/medal_platinum.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            birds = ImageIO.read(new File("Assets/bird_sprite.png"));


        } catch (IOException e) {
        }

        bird_anim = new Animation(70);
        AFrameOnImage f;
        f = new AFrameOnImage(0, 0, 60, 60);
        bird_anim.AddFrame(f);
        f = new AFrameOnImage(60, 0, 60, 60);
        bird_anim.AddFrame(f);
        f = new AFrameOnImage(120, 0, 60, 60);
        bird_anim.AddFrame(f);
        f = new AFrameOnImage(60, 0, 60, 60);
        bird_anim.AddFrame(f);

        bird = new Bird(300, 230, 50, 50);
        ground = new Ground();

        chimneyGroup = new ChimneyGroup();

        leaderboardManager = new LeaderboardManager();

        BeginGame();
    }

    public static void main(String[] args) throws IOException {
        new FlappyBirds();
    }

    private void resetGame() {
        bird.setPos(300, 230);
        bird.setVt(0);
        bird.setLive(true);
        Point = 0;
        chimneyGroup.resetChimneys();
    }

    private void checkScore() {
        for (int i = 0; i < ChimneyGroup.SIZE; i++) {
            if (bird.getPosX() > chimneyGroup.getChimney(i).getPosX() && !chimneyGroup.getChimney(i).getIsBehindBird()
                    && i % 2 == 0) {
                Point++;
                bird.getMoneySound.play();
                chimneyGroup.getChimney(i).setIsBehindBird(true);
            }
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < ChimneyGroup.SIZE; i++) {
            if (bird.getRect().intersects(chimneyGroup.getChimney(i).getRect())) {
                if (bird.getLive()) {
                    bird.bupSound.play();
                }
                bird.setLive(false);

            }
        }

        if (bird.getPosY() + bird.getH() > ground.getYGround()) {
            if (bird.getLive()) {
                bird.bupSound.play();
                bird.setLive(false);
            }
            currentScreen = GAMEOVER_SCREEN;
        }

        if (Point > bestScore) {
            bestScore = Point;
        }

        checkScore();
    }

    private BufferedImage getMedalForScore(int score) {
        if (score >= 50) {
            return platinumMedal;
        } else if (score >= 30) {
            return goldMedal;
        } else if (score >= 15) {
            return silverMedal;
        } else if (score >= 5) {
            return bronzeMedal;
        }
        return null;
    }

    @Override
    public void GAME_UPDATE(long deltaTime) {

        if (isPaused) {
            return; // Skip updates if paused
        }

        if (currentScreen == BEGIN_SCREEN) {
            resetGame();
        } else if (currentScreen == GAMEPLAY_SCREEN) {

            if (bird.getLive())
                bird_anim.Update_Me(deltaTime);

            bird.update(deltaTime);

            if (bird.getLive()) {
                ground.Update();
                chimneyGroup.update();
            }

            checkCollisions();

        } else {

        }

    }

    private void drawScrollbar(Graphics2D g2) {
        List<Player> players = leaderboardManager.getLeaderboard();
        if (players.size() <= VISIBLE_ENTRIES) {
            return; // No need for scrollbar if all entries fit
        }

        int scrollbarX = LEADERBOARD_X + LEADERBOARD_WIDTH - SCROLLBAR_WIDTH;
        int scrollbarY = LEADERBOARD_Y;
        int scrollbarTrackHeight = LEADERBOARD_HEIGHT;

        // Draw scrollbar track
        g2.setColor(new Color(180, 180, 180));
        g2.fillRoundRect(scrollbarX, scrollbarY, SCROLLBAR_WIDTH, scrollbarTrackHeight, 8, 8);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(scrollbarX, scrollbarY, SCROLLBAR_WIDTH, scrollbarTrackHeight, 8, 8);

        // Calculate scrollbar thumb properties
        int maxScroll = Math.max(0, (players.size() - VISIBLE_ENTRIES) * ENTRY_HEIGHT);
        if (maxScroll > 0) {
            float scrollRatio = (float) scrollOffset / maxScroll;
            int thumbHeight = Math.max(20, (int) ((float) scrollbarTrackHeight * VISIBLE_ENTRIES / players.size()));
            int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarTrackHeight - thumbHeight));

            // Draw scrollbar thumb
            g2.setColor(new Color(100, 100, 100));
            g2.fillRoundRect(scrollbarX + 2, thumbY, SCROLLBAR_WIDTH - 4, thumbHeight, 6, 6);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(scrollbarX + 2, thumbY, SCROLLBAR_WIDTH - 4, thumbHeight, 6, 6);
        }
    }

    @Override
    public void GAME_PAINT(Graphics2D g2) {

        if (backgroundDay != null) {
            g2.drawImage(backgroundDay, 0, 0, getWidth(), getHeight(), null);
        }

        chimneyGroup.paint(g2);

        ground.Paint(g2);

        if (currentScreen == MENU_SCREEN) {
            g2.drawImage(titleImage, 160, 100, 400, 100, null);
            g2.drawImage(bird_icon, 590, 120, 60, 50, null);
            g2.drawImage(playButton, 310, 335, 180, 100, null);
            g2.drawImage(exitButton, 341, 450, 120, 50, null);
            return;
        }

        String scoreText = "" + Point;
        Font scoreFont = new Font("Verdana", Font.BOLD, 50);

        // Create the outline shape from the text
        GlyphVector gv = scoreFont.createGlyphVector(g2.getFontRenderContext(), scoreText);
        Shape textShape = gv.getOutline(400, 60);


        BufferedImage buttonToDraw = isPaused ? resumeButton : pauseButton;
        if (buttonToDraw != null) {
            g2.drawImage(buttonToDraw, 20, 20, 40, 45, null);
        }

        if (bird.getIsFlying())
            bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, -1);
        else
            bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, 0);


        if (currentScreen == BEGIN_SCREEN) {
            // Draw the black outline
            g2.setFont(scoreFont);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(5)); // Thickness of the outline
            g2.draw(textShape);

            // Fill with white text inside
            g2.setColor(Color.WHITE);
            g2.fill(textShape);

            g2.drawImage(getReady, 220, 100, 400, 100, null);
            g2.drawImage(instructions, 325, 215, 200, 110, null);
            g2.drawImage(promptToPlay, 285, 325, 280, 90, null);
        } else if (currentScreen == GAMEPLAY_SCREEN) {

            // Draw the black outline
            g2.setFont(scoreFont);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(5)); // Thickness of the outline
            g2.draw(textShape);

            // Fill with white text inside
            g2.setColor(Color.WHITE);
            g2.fill(textShape);

        } else if (currentScreen == GAMEOVER_SCREEN) {

            g2.drawImage(gameOver, 213, 50, 400, 80, null);
            g2.drawImage(panelScore, 215, 155, 400, 200, null);

            g2.drawImage(restartButton, 240, 370, 110, 60, null);
            g2.drawImage(leaderboardButton, 360, 370, 110, 60, null);
            g2.drawImage(menuButton, 480, 370, 110, 60, null);

            // Draw score on top of the panel
            String scoreText1 = "" + Point;
            String scoreText2 = "" + bestScore;
            Font scoreFont1 = new Font("Verdana", Font.BOLD, 18);

            GlyphVector gv1 = scoreFont.createGlyphVector(g2.getFontRenderContext(), scoreText1);
            GlyphVector gv2 = scoreFont.createGlyphVector(g2.getFontRenderContext(), scoreText2);

            Shape textShape1 = gv1.getOutline(520, 250);
            Shape textShape2 = gv2.getOutline(520, 325);


            // Draw the black outline
            g2.setFont(scoreFont1);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(5)); // Thickness of the outline
            g2.draw(textShape1);

            // Fill with white text inside
            g2.setColor(Color.WHITE);
            g2.fill(textShape1);

            // Draw the black outline
            g2.setFont(scoreFont1);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(5)); // Thickness of the outline
            g2.draw(textShape2);

            // Fill with white text inside
            g2.setColor(Color.WHITE);
            g2.fill(textShape2);

            // Draw the medal based on the score
            BufferedImage medalToDraw = getMedalForScore(Point);
            if (medalToDraw != null) {
                g2.drawImage(medalToDraw, 260, 230, 77, 77, null);
            }
        } else if (currentScreen == LEADERBOARD_SCREEN) {

            // Draw custom background panel
            g2.setColor(new Color(222, 96, 54));
            g2.fillRoundRect(180, 40, 440, 350, 20, 20);

            g2.setColor(new Color(255, 199, 120)); // inner lighter tone
            g2.fillRoundRect(190, 50, 420, 330, 15, 15);

            // Border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(4));
            g2.drawRoundRect(190, 50, 420, 330, 15, 15);

            //leaderboard title
            g2.drawImage(leaderboardTitle, 250, 20, 300, 50, null);

            // Header
            g2.setFont(new Font("Courier New", Font.BOLD, 22));
            g2.setColor(Color.BLACK);
            g2.drawString("NAME", 220, 105);
            g2.drawString("SCORE", 490, 105);

            // Draw separator line under header
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(210, 110, 580, 110);

            // Create clipping area for the scrollable content
            Shape oldClip = g2.getClip();
            g2.setClip(LEADERBOARD_X, LEADERBOARD_Y, LEADERBOARD_WIDTH - SCROLLBAR_WIDTH - 5, LEADERBOARD_HEIGHT);

            // Draw scrollable leaderboard entries
            List<Player> players = leaderboardManager.getLeaderboard();
            g2.setFont(new Font("Courier New", Font.PLAIN, 18));
            g2.setColor(Color.BLACK);

            for (int i = 0; i < players.size(); i++) {
                int y = LEADERBOARD_Y + 15 + (i * ENTRY_HEIGHT) - scrollOffset;

                // Only draw entries that are visible in the clipping area
                if (y >= LEADERBOARD_Y - ENTRY_HEIGHT && y <= LEADERBOARD_Y + LEADERBOARD_HEIGHT + ENTRY_HEIGHT) {
                    Player p = players.get(i);
                    String rank = (i + 1) + ".";
                    String displayName = p.getName();

                    // Truncate name if too long
                    if (displayName.length() > 12) {
                        displayName = displayName.substring(0, 10) + "..";
                    }

                    String score = String.format("%05d", p.getScore());

                    // Alternate row colors for better readability
                    if (i % 2 == 0) {
                        g2.setColor(new Color(255, 255, 255, 50));
                        g2.fillRect(LEADERBOARD_X, y - 15, LEADERBOARD_WIDTH - SCROLLBAR_WIDTH - 5, ENTRY_HEIGHT);
                    }

                    g2.setColor(Color.BLACK);
                    g2.drawString(rank + " " + displayName, LEADERBOARD_X + 20, y);
                    g2.drawString(score, LEADERBOARD_X + 290, y);
                }
            }

            // Restore original clipping
            g2.setClip(oldClip);

            // Draw scrollbar
            drawScrollbar(g2);

            // Draw the back button area manually
            GradientPaint gradient1 = new GradientPaint(330, 500, new Color(255, 150, 100), 470, 540, new Color(255, 100, 60));
            g2.setPaint(gradient1);
            g2.fillRoundRect(330, 400, 140, 40, 20, 20);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(330, 400, 140, 40, 20, 20);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Courier New", Font.BOLD, 16));
            g2.drawString("BACK", 370, 425);

            // Draw the add button
            GradientPaint gradient2 = new GradientPaint(490, 500, new Color(60, 180, 75), 530, 540, new Color(40, 140, 55));
            g2.setPaint(gradient2);
            g2.fillRoundRect(589, 30, 40, 40, 15, 15);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Courier New", Font.BOLD, 24));
            g2.drawString("+", 602, 52);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(589, 30, 40, 40, 15, 15);
        }
    }


    @Override
    public void KEY_ACTION(KeyEvent e, int Event) {
        if (Event == KEY_PRESSED) {

            // SPACE Key
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (currentScreen == BEGIN_SCREEN) {
                    currentScreen = GAMEPLAY_SCREEN;
                } else if (currentScreen == GAMEPLAY_SCREEN) {
                    if (bird.getLive() && !isPaused)
                        bird.fly();
                }
            }

            // ESC Key
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && currentScreen == GAMEPLAY_SCREEN) {
                isPaused = !isPaused;
                System.out.println("Game Paused? " + isPaused);
            }
        }
    }

    private boolean isPointInScrollbar(int mouseX, int mouseY) {
        int scrollbarX = LEADERBOARD_X + LEADERBOARD_WIDTH - SCROLLBAR_WIDTH;
        int scrollbarY = LEADERBOARD_Y;
        int scrollbarTrackHeight = LEADERBOARD_HEIGHT;

        return mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarTrackHeight;
    }

    private void handleScrollbarDrag(int mouseY) {
        List<Player> players = leaderboardManager.getLeaderboard();
        if (players.size() <= VISIBLE_ENTRIES) {
            return;
        }

        int scrollbarTrackHeight = LEADERBOARD_HEIGHT;
        int maxScroll = Math.max(0, (players.size() - VISIBLE_ENTRIES) * ENTRY_HEIGHT);

        int relativeY = mouseY - LEADERBOARD_Y - scrollbarDragOffset;
        float scrollRatio = (float) relativeY / scrollbarTrackHeight;
        scrollRatio = Math.max(0, Math.min(1, scrollRatio));

        scrollOffset = (int) (scrollRatio * maxScroll);
    }

    @Override
    public void MOUSE_ACTION(MouseEvent e, int Event) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (Event == MouseEvent.MOUSE_PRESSED && currentScreen == LEADERBOARD_SCREEN) {
            if (isPointInScrollbar(mouseX, mouseY)) {
                isDraggingScrollbar = true;

                // Calculate thumb position and drag offset
                List<Player> players = leaderboardManager.getLeaderboard();
                if (players.size() > VISIBLE_ENTRIES) {
                    int maxScroll = Math.max(0, (players.size() - VISIBLE_ENTRIES) * ENTRY_HEIGHT);
                    float scrollRatio = (float) scrollOffset / maxScroll;
                    int thumbHeight = Math.max(20, (int) ((float) LEADERBOARD_HEIGHT * VISIBLE_ENTRIES / players.size()));
                    int thumbY = LEADERBOARD_Y + (int) (scrollRatio * (LEADERBOARD_HEIGHT - thumbHeight));

                    scrollbarDragOffset = mouseY - thumbY;
                }
                return;
            }
        }

        if (Event == MouseEvent.MOUSE_RELEASED) {
            isDraggingScrollbar = false;
        }

        if (Event == MouseEvent.MOUSE_DRAGGED && isDraggingScrollbar) {
            handleScrollbarDrag(mouseY);
            return;
        }

        if (Event == MouseEvent.MOUSE_CLICKED) {
            System.out.println("Mouse clicked at: " + mouseX + "," + mouseY);

            if (currentScreen == MENU_SCREEN) {
                // Play button position and size
                int playbtnX = 310, playbtnY = 335, playbtnWidth = 180, playbtnHeight = 100;
                int exitbtnX = 341, exitbtnY = 450, exitbtnWidth = 120, exitbtnHeight = 50;

                // If click is inside the play button area
                if (mouseX >= playbtnX && mouseX <= playbtnX + playbtnWidth && mouseY >= playbtnY && mouseY <= playbtnY + playbtnHeight) {
                    currentScreen = BEGIN_SCREEN;
                }

                // If click is inside the exit button area
                if (mouseX >= exitbtnX && mouseX <= exitbtnX + exitbtnWidth && mouseY >= exitbtnY && mouseY <= exitbtnY + exitbtnHeight) {

                    System.exit(0);
                }
            } else if (currentScreen == GAMEPLAY_SCREEN) {
                // Pause button position and size
                int pausebtnX = 20, pausebtnY = 20, pausebtnWidth = 40, pausebtnHeight = 45;

                // If click is inside the pause button area
                if (mouseX >= pausebtnX && mouseX <= pausebtnX + pausebtnWidth && mouseY >= pausebtnY && mouseY <= pausebtnY + pausebtnHeight) {
                    isPaused = !isPaused;  // Toggle pause
                    System.out.println("Game Paused? " + isPaused);
                }
            } else if (currentScreen == GAMEOVER_SCREEN) {

                int restartBtnX = 240, restartBtnY = 370, restartBtnWidth = 110, restartBtnHeight = 60;
                int leaderboardBtnX = 360, leaderboardBtnY = 370, leaderboardBtnWidth = 110, leaderboardBtnHeight = 60;
                int menuBtnX = 480, menuBtnY = 370, menuBtnWidth = 110, menuBtnHeight = 60;


                // If click is inside the restart button area
                if (mouseX >= restartBtnX && mouseX <= restartBtnX + restartBtnWidth && mouseY >= restartBtnY && mouseY <= restartBtnY + restartBtnHeight) {
                    resetGame();
                    currentScreen = BEGIN_SCREEN;
                }

                // If click is inside the leaderboard button area
                if (mouseX >= leaderboardBtnX && mouseX <= leaderboardBtnX + leaderboardBtnWidth && mouseY >= leaderboardBtnY && mouseY <= leaderboardBtnY + leaderboardBtnHeight) {
                    System.out.println("Leaderboard button clicked");
                    currentScreen = LEADERBOARD_SCREEN; // Switch to leaderboard screen
                    scrollOffset = 0; // Reset scroll position when entering leaderboard
                }

                // If click is inside the menu button area
                if (mouseX >= menuBtnX && mouseX <= menuBtnX + menuBtnWidth && mouseY >= menuBtnY && mouseY <= menuBtnY + menuBtnHeight) {
                    currentScreen = MENU_SCREEN;
                }
            } else if (currentScreen == LEADERBOARD_SCREEN) {

                // Back button position and size
                int backBtnX = 330, backBtnY = 400, backBtnWidth = 140, backBtnHeight = 40;

                if (mouseX >= backBtnX && mouseX <= backBtnX + backBtnWidth && mouseY >= backBtnY && mouseY <= backBtnY + backBtnHeight) {
                    currentScreen = GAMEOVER_SCREEN;
                }

                int addBtnX = 589, addBtnY = 30, addBtnWidth = 40, addBtnHeight = 40;
                if (mouseX >= addBtnX && mouseX <= addBtnX + addBtnWidth && mouseY >= addBtnY && mouseY <= addBtnY + addBtnHeight) {
                    String name = JOptionPane.showInputDialog(this, "Enter your name:");
                    if (name != null && !name.trim().isEmpty()) {
                        Player newPlayer = new Player(name.trim(), Point);
                        leaderboardManager.addPlayer(name.trim().toUpperCase(), Point);
                    }
                }
            }
        }
    }

    @Override
    public void MOUSE_WHEEL_ACTION(int notches) {
        if (currentScreen == LEADERBOARD_SCREEN) {
            List<Player> players = leaderboardManager.getLeaderboard();
            int maxOffset = Math.max(0, (players.size() - VISIBLE_ENTRIES) * ENTRY_HEIGHT);
            scrollOffset += notches * ENTRY_HEIGHT;
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxOffset));
        }
    }
}