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
    private BufferedImage label_new;

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
    private boolean isNewBestScore = false;

    private LeaderboardManager leaderboardManager = new LeaderboardManager();

    private int scrollOffset = 0;
    private final int ENTRY_HEIGHT = 30;
    private final int VISIBLE_ENTRIES = 9;

    // Scrollbar properties
    private final int SCROLLBAR_WIDTH = 15;
    private final int LEADERBOARD_X = 205;
    private final int LEADERBOARD_Y = 105;
    private final int LEADERBOARD_WIDTH = 400;
    private final int LEADERBOARD_HEIGHT = VISIBLE_ENTRIES * ENTRY_HEIGHT;
    private boolean isDraggingScrollbar = false;
    private int scrollbarDragOffset = 0;

    // Button hover states
    private int hoveredButton = -1; // -1 means no button is hovered
    private final int BUTTON_PLAY = 1;
    private final int BUTTON_EXIT = 2;
    private final int BUTTON_PAUSE = 3;
    private final int BUTTON_RESTART = 4;
    private final int BUTTON_LEADERBOARD = 5;
    private final int BUTTON_MENU = 6;
    private final int BUTTON_BACK = 7;
    private final int BUTTON_ADD = 8;
    private final int BUTTON_SEARCH = 9;

    // Button hover colors
    private final Color HOVER_OVERLAY = new Color(246, 1, 1, 80); // Semi-transparent white
    private final Color BUTTON_HOVER_GLOW = new Color(128, 128, 128); // Golden glow

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
            label_new = ImageIO.read(
                    new File("Assets/label_new.png"));
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

            if (Point > bestScore) {
                bestScore = Point;
                isNewBestScore = true;
            } else {
                isNewBestScore = false;
            }
            currentScreen = GAMEOVER_SCREEN;
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

    // Method to check which button is being hovered
    private int getHoveredButton(int mouseX, int mouseY) {
        if (currentScreen == MENU_SCREEN) {
            // Play button
            if (mouseX >= 310 && mouseX <= 490 && mouseY >= 335 && mouseY <= 435) {
                return BUTTON_PLAY;
            }
            // Exit button
            if (mouseX >= 341 && mouseX <= 461 && mouseY >= 450 && mouseY <= 500) {
                return BUTTON_EXIT;
            }
        } else if (currentScreen == GAMEPLAY_SCREEN) {
            // Pause/Resume button
            if (mouseX >= 20 && mouseX <= 60 && mouseY >= 20 && mouseY <= 65) {
                return BUTTON_PAUSE;
            }
        } else if (currentScreen == GAMEOVER_SCREEN) {
            // Restart button
            if (mouseX >= 240 && mouseX <= 350 && mouseY >= 370 && mouseY <= 430) {
                return BUTTON_RESTART;
            }
            // Leaderboard button
            if (mouseX >= 360 && mouseX <= 470 && mouseY >= 370 && mouseY <= 430) {
                return BUTTON_LEADERBOARD;
            }
            // Menu button
            if (mouseX >= 480 && mouseX <= 590 && mouseY >= 370 && mouseY <= 430) {
                return BUTTON_MENU;
            }
        } else if (currentScreen == LEADERBOARD_SCREEN) {
            // Back button
            if (mouseX >= 230 && mouseX <= 370 && mouseY >= 400 && mouseY <= 440) {
                return BUTTON_BACK;
            }

            // Search button
            if (mouseX >= 430 && mouseX <= 570 && mouseY >= 400 && mouseY <= 440) {
                return BUTTON_SEARCH;
            }

            // Add button
            if (mouseX >= 589 && mouseX <= 629 && mouseY >= 30 && mouseY <= 70) {
                return BUTTON_ADD;
            }
        }
        return -1; // No button hovered
    }

    // Method to draw hover effect
    private void drawButtonHover(Graphics2D g2, int buttonType, int x, int y, int width, int height) {
        if (hoveredButton == buttonType) {
            // Draw outer glow effect
            g2.setColor(new Color(255, 215, 0, 60)); // Golden glow - more transparent
            g2.setStroke(new BasicStroke(6));
            g2.drawRoundRect(x - 3, y - 3, width + 6, height + 6, 12, 12);

            // Draw inner glow
            g2.setColor(new Color(255, 255, 255, 40)); // White inner glow
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x - 1, y - 1, width + 2, height + 2, 8, 8);

            // Draw subtle overlay
            g2.setColor(HOVER_OVERLAY);
            g2.fillRoundRect(x, y, width, height, 8, 8);

            // Set hand cursor
            setCursor(new Cursor(Cursor.HAND_CURSOR));
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

            // Draw play button with hover effect
            g2.drawImage(playButton, 310, 335, 180, 100, null);
            drawButtonHover(g2, BUTTON_PLAY, 310, 335, 180, 100);

            // Draw exit button with hover effect
            g2.drawImage(exitButton, 341, 450, 120, 50, null);
            drawButtonHover(g2, BUTTON_EXIT, 341, 450, 120, 50);

            return;
        }

        String scoreText = "" + Point;
        Font scoreFont = new Font("Verdana", Font.BOLD, 50);
        GlyphVector gv = scoreFont.createGlyphVector(g2.getFontRenderContext(), scoreText);
        Shape textShape = gv.getOutline(400, 60);

        // Draw pause/resume button with hover effect
        BufferedImage buttonToDraw = isPaused ? resumeButton : pauseButton;
        if (buttonToDraw != null) {
            g2.drawImage(buttonToDraw, 20, 20, 40, 45, null);
            drawButtonHover(g2, BUTTON_PAUSE, 20, 20, 40, 45);
        }

        if (bird.getIsFlying())
            bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, -1);
        else
            bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, 0);

        if (currentScreen == BEGIN_SCREEN) {
            // Draw score with outline
            g2.setFont(scoreFont);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(5));
            g2.draw(textShape);
            g2.setColor(Color.WHITE);
            g2.fill(textShape);

            g2.drawImage(getReady, 220, 100, 400, 100, null);
            g2.drawImage(instructions, 325, 215, 200, 110, null);
            g2.drawImage(promptToPlay, 285, 325, 280, 90, null);

        } else if (currentScreen == GAMEPLAY_SCREEN) {
            // Draw score with outline
            g2.setFont(scoreFont);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(5));
            g2.draw(textShape);
            g2.setColor(Color.WHITE);
            g2.fill(textShape);

        } else if (currentScreen == GAMEOVER_SCREEN) {
            g2.drawImage(gameOver, 213, 50, 400, 80, null);
            g2.drawImage(panelScore, 215, 155, 400, 200, null);

            // Draw buttons with hover effects
            g2.drawImage(restartButton, 240, 370, 110, 60, null);
            drawButtonHover(g2, BUTTON_RESTART, 240, 370, 110, 60);

            g2.drawImage(leaderboardButton, 360, 370, 110, 60, null);
            drawButtonHover(g2, BUTTON_LEADERBOARD, 360, 370, 110, 60);

            g2.drawImage(menuButton, 480, 370, 110, 60, null);
            drawButtonHover(g2, BUTTON_MENU, 480, 370, 110, 60);

            // Draw scores
            String scoreText1 = "" + Point;
            String scoreText2 = "" + bestScore;
            Font scoreFont1 = new Font("Verdana", Font.BOLD, 18);

            GlyphVector gv1 = scoreFont.createGlyphVector(g2.getFontRenderContext(), scoreText1);
            GlyphVector gv2 = scoreFont.createGlyphVector(g2.getFontRenderContext(), scoreText2);

            Shape textShape1 = gv1.getOutline(520, 250);
            Shape textShape2 = gv2.getOutline(520, 325);

            // Draw score outlines and fills
            g2.setFont(scoreFont1);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(5));
            g2.draw(textShape1);
            g2.draw(textShape2);
            g2.setColor(Color.WHITE);
            g2.fill(textShape1);
            g2.fill(textShape2);

            // Draw "NEW" label
            if (isNewBestScore && label_new != null) {
                g2.drawImage(label_new, 475, 258, 35, 23, null);
            }

            // Draw medal
            BufferedImage medalToDraw = getMedalForScore(Point);
            if (medalToDraw != null) {
                g2.drawImage(medalToDraw, 260, 230, 77, 77, null);
            }

        } else if (currentScreen == LEADERBOARD_SCREEN) {
            // Draw custom background panel
            g2.setColor(new Color(222, 96, 54));
            g2.fillRoundRect(180, 40, 440, 350, 20, 20);

            g2.setColor(new Color(255, 199, 120));
            g2.fillRoundRect(190, 50, 420, 330, 15, 15);

            // Border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(4));
            g2.drawRoundRect(190, 50, 420, 330, 15, 15);

            // Leaderboard title
            g2.drawImage(leaderboardTitle, 250, 20, 300, 55, null);

            // Header
            g2.setFont(new Font("Courier New", Font.BOLD, 22));
            g2.setColor(Color.BLACK);
            g2.drawString("NAME", 255, 99);
            g2.drawString("SCORE", 490, 99);

            // Draw separator line under header
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(210, 103, 580, 103);

            // Create clipping area for the scrollable content
            Shape oldClip = g2.getClip();
            g2.setClip(LEADERBOARD_X, LEADERBOARD_Y, LEADERBOARD_WIDTH - SCROLLBAR_WIDTH - 5, LEADERBOARD_HEIGHT);

            // Draw scrollable leaderboard entries
            List<Player> players = leaderboardManager.getLeaderboard();
            g2.setFont(new Font("Courier New", Font.PLAIN, 18));
            g2.setColor(Color.BLACK);

            for (int i = 0; i < players.size(); i++) {
                int y = LEADERBOARD_Y + 15 + (i * ENTRY_HEIGHT) - scrollOffset;

                if (y >= LEADERBOARD_Y - ENTRY_HEIGHT && y <= LEADERBOARD_Y + LEADERBOARD_HEIGHT + ENTRY_HEIGHT) {
                    Player p = players.get(i);
                    String rank = (i + 1) + ".";
                    String displayName = p.getName();

                    if (displayName.length() > 12) {
                        displayName = displayName.substring(0, 10) + "..";
                    }

                    String score = String.format("%05d", p.getScore());

                    // Alternate row colors
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

            // Draw back button with hover effect
            GradientPaint gradient1 = new GradientPaint(330, 400, new Color(255, 150, 100), 470, 440, new Color(255, 100, 60));
            g2.setPaint(gradient1);
            g2.fillRoundRect(230, 400, 140, 40, 20, 20);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(230, 400, 140, 40, 20, 20);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Courier New", Font.BOLD, 35));
            g2.drawString("BACK", 258, 430);

            // Draw hover effect for back button
            drawButtonHover(g2, BUTTON_BACK, 230, 400, 140, 40);

            // Draw the search button
            GradientPaint searchGradient = new GradientPaint(530, 500, new Color(100, 150, 255), 570, 540, new Color(50, 100, 200));
            g2.setPaint(searchGradient);
            g2.fillRoundRect(430, 400, 140, 40, 15, 15);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Courier New", Font.BOLD, 35));
            g2.drawString("Search", 438, 430);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(430, 400, 140, 40, 15, 15);

            // Draw hover effect for add button
            drawButtonHover(g2, BUTTON_SEARCH, 430, 400, 140, 40);


            // Draw add button with hover effect
            GradientPaint gradient2 = new GradientPaint(589, 30, new Color(60, 180, 75), 629, 70, new Color(40, 140, 55));
            g2.setPaint(gradient2);
            g2.fillRoundRect(589, 30, 40, 40, 15, 15);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Courier New", Font.BOLD, 50));
            g2.drawString("+", 595, 63);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(589, 30, 40, 40, 15, 15);

            // Draw hover effect for add button
            drawButtonHover(g2, BUTTON_ADD, 589, 30, 40, 40);

        }

        // Reset cursor if no button is hovered
        if (hoveredButton == -1) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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

    private boolean isPointInScrollbarThumb(int mouseX, int mouseY) {
        List<Player> players = leaderboardManager.getLeaderboard();
        if (players.size() <= VISIBLE_ENTRIES) {
            return false;
        }

        int scrollbarX = LEADERBOARD_X + LEADERBOARD_WIDTH - SCROLLBAR_WIDTH;
        int scrollbarY = LEADERBOARD_Y;
        int scrollbarTrackHeight = LEADERBOARD_HEIGHT;

        int maxScroll = Math.max(0, (players.size() - VISIBLE_ENTRIES) * ENTRY_HEIGHT);
        float scrollRatio = (float) scrollOffset / maxScroll;
        int thumbHeight = Math.max(20, (int) ((float) scrollbarTrackHeight * VISIBLE_ENTRIES / players.size()));
        int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarTrackHeight - thumbHeight));

        return mouseX >= scrollbarX + 2 && mouseX <= scrollbarX + SCROLLBAR_WIDTH - 2 &&
                mouseY >= thumbY && mouseY <= thumbY + thumbHeight;
    }


    private void handleScrollbarDrag(int mouseY) {
        List<Player> players = leaderboardManager.getLeaderboard();
        if (players.size() <= VISIBLE_ENTRIES) {
            return;
        }

        int scrollbarTrackHeight = LEADERBOARD_HEIGHT;
        int maxScroll = Math.max(0, (players.size() - VISIBLE_ENTRIES) * ENTRY_HEIGHT);

        // Calculate thumb height
        int thumbHeight = Math.max(20, (int) ((float) scrollbarTrackHeight * VISIBLE_ENTRIES / players.size()));
        int usableTrackHeight = scrollbarTrackHeight - thumbHeight;

        // Calculate relative position considering the drag offset
        int relativeY = mouseY - LEADERBOARD_Y - scrollbarDragOffset;

        // Ensure we don't go beyond the usable track area
        relativeY = Math.max(0, Math.min(relativeY, usableTrackHeight));

        // Calculate scroll ratio
        float scrollRatio = usableTrackHeight > 0 ? (float) relativeY / usableTrackHeight : 0;

        // Apply the scroll
        scrollOffset = (int) (scrollRatio * maxScroll);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
    }

    private void updateHoverState(int mouseX, int mouseY) {
        int newHoveredButton = getHoveredButton(mouseX, mouseY);
        if (newHoveredButton != hoveredButton) {
            hoveredButton = newHoveredButton;
            repaint(); // Trigger repaint to show hover effects
        }
    }


    @Override
    public void MOUSE_ACTION(MouseEvent e, int Event) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        // Update hovered button on both mouse move and drag
        if (Event == MouseEvent.MOUSE_MOVED || Event == MouseEvent.MOUSE_DRAGGED) {
            // Only update hover if not dragging scrollbar
            if (!isDraggingScrollbar) {
                updateHoverState(mouseX, mouseY);
            }

            // Handle scrollbar dragging
            if (Event == MouseEvent.MOUSE_DRAGGED && isDraggingScrollbar) {
                handleScrollbarDrag(mouseY);
            }
            return;
        }

        // Handle mouse press events for scrollbar
        if (Event == MouseEvent.MOUSE_PRESSED && currentScreen == LEADERBOARD_SCREEN) {
            if (isPointInScrollbar(mouseX, mouseY)) {
                isDraggingScrollbar = true;

                if (isPointInScrollbarThumb(mouseX, mouseY)) {
                    List<Player> players = leaderboardManager.getLeaderboard();
                    if (players.size() > VISIBLE_ENTRIES) {
                        int maxScroll = Math.max(0, (players.size() - VISIBLE_ENTRIES) * ENTRY_HEIGHT);
                        float scrollRatio = (float) scrollOffset / maxScroll;
                        int thumbHeight = Math.max(20, (int) ((float) LEADERBOARD_HEIGHT * VISIBLE_ENTRIES / players.size()));
                        int thumbY = LEADERBOARD_Y + (int) (scrollRatio * (LEADERBOARD_HEIGHT - thumbHeight));

                        scrollbarDragOffset = mouseY - thumbY;
                    }
                } else {
                    scrollbarDragOffset = 10;
                    handleScrollbarDrag(mouseY);
                }
                return;
            }
        }

        // Handle mouse release events
        if (Event == MouseEvent.MOUSE_RELEASED) {
            isDraggingScrollbar = false;
            scrollbarDragOffset = 0;
            // Update hover state after release
            updateHoverState(mouseX, mouseY);
        }

        // Handle mouse click events
        if (Event == MouseEvent.MOUSE_CLICKED) {
            System.out.println("Mouse clicked at: " + mouseX + "," + mouseY);

            if (currentScreen == MENU_SCREEN) {
                // Play button
                if (mouseX >= 310 && mouseX <= 490 && mouseY >= 335 && mouseY <= 435) {
                    currentScreen = BEGIN_SCREEN;
                    hoveredButton = -1; // Reset hover state
                }
                // Exit button
                else if (mouseX >= 341 && mouseX <= 461 && mouseY >= 450 && mouseY <= 500) {
                    System.exit(0);
                }
            } else if (currentScreen == GAMEPLAY_SCREEN) {
                // Pause/Resume button
                if (mouseX >= 20 && mouseX <= 60 && mouseY >= 20 && mouseY <= 65) {
                    isPaused = !isPaused;
                    System.out.println("Game Paused? " + isPaused);
                }
            } else if (currentScreen == GAMEOVER_SCREEN) {
                // Restart button
                if (mouseX >= 240 && mouseX <= 350 && mouseY >= 370 && mouseY <= 430) {
                    resetGame();
                    currentScreen = BEGIN_SCREEN;
                    hoveredButton = -1;
                }
                // Leaderboard button
                else if (mouseX >= 360 && mouseX <= 470 && mouseY >= 370 && mouseY <= 430) {
                    System.out.println("Leaderboard button clicked");
                    currentScreen = LEADERBOARD_SCREEN;
                    scrollOffset = 0;
                    hoveredButton = -1;
                }
                // Menu button
                else if (mouseX >= 480 && mouseX <= 590 && mouseY >= 370 && mouseY <= 430) {
                    currentScreen = MENU_SCREEN;
                    hoveredButton = -1;
                }
            } else if (currentScreen == LEADERBOARD_SCREEN) {

                // Back button
                if (mouseX >= 230 && mouseX <= 370 && mouseY >= 400 && mouseY <= 440) {
                    currentScreen = GAMEOVER_SCREEN;
                    hoveredButton = -1;
                }

                // Add button
                else if (mouseX >= 589 && mouseX <= 629 && mouseY >= 30 && mouseY <= 70) {
                    String name = JOptionPane.showInputDialog(null, "Enter your name:");
                    if (name != null && !name.trim().isEmpty()) {
                        name = name.trim().toUpperCase();

                        if (leaderboardManager.playerExists(name)) {
                            JOptionPane.showMessageDialog(null, "This player name already exists in the leaderboard. Please choose a different name.");
                        } else {
                            leaderboardManager.addPlayer(name, Point);
                        }
                    }
                }

                // Search button
                else if (mouseX >= 430 && mouseX <= 570 && mouseY >= 400 && mouseY <= 440) {
                    String searchName = JOptionPane.showInputDialog(null, "Enter player name to search:");
                    if (searchName != null && !searchName.trim().isEmpty()) {
                        Player foundPlayer = leaderboardManager.searchPlayerByName(searchName.trim());
                        int rank = leaderboardManager.getPlayerRank(searchName.trim());
                        if (foundPlayer != null && rank != -1) {
                            JOptionPane.showMessageDialog(this, "Found: " + foundPlayer.getName() +
                                    " | Score: " + foundPlayer.getScore() + " | Rank: #" + rank);
                        } else {
                            JOptionPane.showMessageDialog(null, "Player not found.");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void MOUSE_EXITED(MouseEvent e) {
        if (hoveredButton != -1) {
            hoveredButton = -1;
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            repaint();
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