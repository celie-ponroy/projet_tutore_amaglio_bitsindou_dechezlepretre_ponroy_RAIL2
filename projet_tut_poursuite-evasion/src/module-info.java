module com.example.projet_tut_poursuiteevasion {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens moteur to javafx.fxml;
    exports moteur;
    exports affichage;
    opens affichage to javafx.fxml;

}