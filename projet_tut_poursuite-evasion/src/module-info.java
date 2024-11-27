module com.example.projet_tut_poursuiteevasion {
    requires javafx.controls;
    requires javafx.fxml;

    opens moteur to javafx.fxml;
    exports moteur;
    exports affichage;
    opens affichage to javafx.fxml;
}