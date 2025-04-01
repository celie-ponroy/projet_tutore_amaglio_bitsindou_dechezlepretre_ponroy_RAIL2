module com.example.projet_tut_poursuiteevasion {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires javafx.media;

    requires ai.djl.api;
    requires ai.djl.basicdataset;
    requires org.apache.commons.csv;
    requires ai.djl.model_zoo;

    opens moteur to javafx.fxml;
    exports moteur;
    exports affichage;
    opens affichage to javafx.fxml;
    exports affichage.filtres;
    opens affichage.filtres to javafx.fxml;

}