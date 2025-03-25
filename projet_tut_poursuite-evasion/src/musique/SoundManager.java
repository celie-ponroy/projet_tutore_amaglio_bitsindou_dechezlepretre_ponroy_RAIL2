package musique;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.nio.file.Paths;

public class SoundManager {

    // Attributs
    private MediaPlayer fondMusic; // Musique de fond
    private MediaPlayer winMusic; // Musique de victoire
    private MediaPlayer looseMusic; // Musique de défaite
    private MediaPlayer drawMusic; // Musique de match nul
    private MediaPlayer waitMusic; // Musique de test

    public SoundManager() {
        fondMusic = createMediaPlayer("sons/fond.mp3");
        winMusic = createMediaPlayer("sons/win.mp3");
        looseMusic = createMediaPlayer("sons/loose.mp3");
        drawMusic = createMediaPlayer("sons/draw.mp3");
    }

    /**
     * Crée un lecteur de musique à partir d'un fichier
     * @param filePath
     * @return
     */
    private MediaPlayer createMediaPlayer(String filePath) {
        Media media = new Media(Paths.get(filePath).toUri().toString());
        return new MediaPlayer(media);
    }

    public void playFondMusic() {
        fondMusic.play();
    }

    public void playWinMusic() {
        winMusic.play();
    }

    public void playLooseMusic() {
        looseMusic.play();
    }

    public void playDrawMusic() {
        drawMusic.play();
    }

    public void playWaitMusic() {
        waitMusic.play();
    }

    public void stopAllSounds() {
        fondMusic.stop();
        winMusic.stop();
        looseMusic.stop();
        drawMusic.stop();
        waitMusic.stop();
    }
}
