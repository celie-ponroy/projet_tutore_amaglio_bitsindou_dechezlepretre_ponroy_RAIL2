package musique;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.nio.file.Paths;

/**
 * Classe pour gérer les musiques du jeu
 */
public class SoundManager {

    // Attributs
    private static MediaPlayer fondMusic; // Musique de fond
    private static MediaPlayer winMusic; // Musique de victoire
    private static MediaPlayer looseMusic; // Musique de défaite
    private static MediaPlayer drawMusic; // Musique de match nul
    private static MediaPlayer gameMusic; // Musique de jeu

    // Constructeur
    public SoundManager() {
        fondMusic = createMediaPlayer("sons/fond.mp3");
        winMusic = createMediaPlayer("sons/win.mp3");
        looseMusic = createMediaPlayer("sons/loose.mp3");
        drawMusic = createMediaPlayer("sons/draw.mp3");
        gameMusic = createMediaPlayer("sons/game.mp3");
    }

    /**
     * Crée un lecteur de musique à partir d'un fichier
     * @param filePath : chemin du fichier de musique
     * @return le lecteur de musique
     */
    private MediaPlayer createMediaPlayer(String filePath) {
        Media media = new Media(Paths.get(filePath).toUri().toString());
        return new MediaPlayer(media);
    }

    /**
     * Méthode pour jouer la musique de fond
     */
    public static void playFondMusic() {
        //joue la musique en boucle
        fondMusic.setCycleCount(MediaPlayer.INDEFINITE);
        fondMusic.play();
    }

    /**
     * Méthode pour jouer la musique de jeu
     */
    public static void playGameMusic() {
        gameMusic.setCycleCount(MediaPlayer.INDEFINITE);
        gameMusic.play();
    }

    /**
     * Méthode pour jouer la musique de victoire
     */
    public static void playWinMusic() {
        winMusic.play();
    }

    /**
     * Méthode pour jouer la musique de défaite
     */
    public static void playLooseMusic() {
        looseMusic.play();
    }

    /**
     * Méthode pour jouer la musique de match nul
     */
    public static void playDrawMusic() {
        drawMusic.play();
    }

    /**
     * Méthode pour arrêter la musique de fond
     */
    public static void stopFondMusic() {
        fondMusic.stop();
    }

    /**
     * Méthode pour arrêter toutes les musiques
     */
    public static void stopAllMusic() {
        if (gameMusic != null) gameMusic.stop();
        if (drawMusic != null) drawMusic.stop();
        if (winMusic != null) winMusic.stop();
        if (looseMusic != null) looseMusic.stop();
    }

}
