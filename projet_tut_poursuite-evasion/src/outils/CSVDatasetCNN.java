package outils;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.dataset.Record;
import ai.djl.util.Progress;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import simulation.Simulation;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CSVDatasetCNN extends RandomAccessDataset implements CSVDataset {

    private final List<CSVRecord> csvRecords;

    private CSVDatasetCNN(Builder builder) {
        super(builder);
        csvRecords = builder.csvRecords;
    }

    @Override
    public Record get(NDManager manager, long index) {
        CSVRecord record = csvRecords.get(Math.toIntExact(index));

        // Conversion de la colonne "mapBayesienne" en un tableau de float
        float[] mapValues = parseMap(record.get("map"));

        // Conversion de la colonne "pos" en un tableau de float
        float[] posValues = parseMap(record.get("pos"));
        float[] posMapValue = new float[Simulation.getTailleCarte()];
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[0].length; j++) {
                if (i == posValues[0] && j == posValues[1]) {
                    posMapValue[(j * Simulation.CARTE[0].length) + i] = 1;
                } else {
                    posMapValue[(Simulation.CARTE[0].length * j) + i] = 0;
                }
            }
        }

        // Conversion de la colonne "realMap" en un tableau de float
        float[] realMapsValues = parseMap(record.get("rmap"));

        //Creation des tenseurs de position et deplacement
        NDArray posR = manager.create(posMapValue).reshape(1, Simulation.CARTE.length, Simulation.CARTE[0].length);

        NDArray dep = manager.create(Float.parseFloat(record.get("dep")));

        // Crée les cartes avec la dimension des canaux
        NDArray bayesien = manager.create(mapValues).reshape(1, Simulation.CARTE.length, Simulation.CARTE[0].length); // (1, H, W)
        NDArray realMap = manager.create(realMapsValues).reshape(1, Simulation.CARTE.length, Simulation.CARTE[0].length); // (1, H, W)

        // Concatène sur l'axe des canaux (0 -> channel)
        NDArray inputData = NDArrays.concat(new NDList(bayesien, realMap, posR)); // Résultat : (3, H, W)
        return new Record(new NDList(inputData), new NDList(dep));
    }

    @Override
    public long availableSize() {
        return csvRecords.size();
    }

    private float[] parseMap(String mapString) {
        // Diviser la chaîne sur les virgules et convertir chaque élément en float
        String[] strs = mapString.split(",");
        float[] mapValues = new float[strs.length];
        for (int i = 0; i < strs.length; i++) {
            mapValues[i] = Float.parseFloat(strs[i]);
        }
        return mapValues;
    }

    @Override
    public void prepare(Progress progress) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends BaseBuilder<Builder> {
        List<CSVRecord> csvRecords;

        public Builder() {
        }

        @Override
        protected Builder self() {
            return this;
        }

        public CSVDatasetCNN build(String nomFichier) throws IOException {
            try (Reader reader = Files.newBufferedReader(Paths.get(nomFichier));
                 CSVParser csvParser =
                         new CSVParser(reader, CSVFormat.DEFAULT
                                 .withHeader("map", "pos", "rmap", "dep")
                                 .withFirstRecordAsHeader()
                                 .withIgnoreHeaderCase()
                                 .withTrim())) {
                csvRecords = csvParser.getRecords();
            }
            return new CSVDatasetCNN(this);
        }
    }
}