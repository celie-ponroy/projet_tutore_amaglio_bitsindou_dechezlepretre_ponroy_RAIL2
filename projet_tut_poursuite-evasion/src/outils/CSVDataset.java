package outils;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.dataset.Record;
import ai.djl.translate.TranslateException;
import ai.djl.util.Progress;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class CSVDataset extends RandomAccessDataset {

    private final List<CSVRecord> csvRecords;

    private CSVDataset(Builder builder) {
        super(builder);
        csvRecords = builder.csvRecords;
    }

    @Override
    public Record get(NDManager manager, long index) {
        CSVRecord record = csvRecords.get(Math.toIntExact(index));

        // Conversion de la colonne "map" en un tableau de float
        float[] mapValues = parseMap(record.get("map"));
        NDArray bayesien = manager.create(mapValues);

        // Conversion de "dep" en float
        NDArray dep = manager.create(Float.parseFloat(record.get("dep")));

        return new Record(new NDList(bayesien), new NDList(dep));
    }

    @Override
    public long availableSize() {
        return csvRecords.size();
    }

    private float[] parseMap(String mapString) {
        // Diviser la chaîne sur les virgules et convertir chaque élément en float
        return Stream.of(mapString.split(","))
                .mapToDouble(Double::parseDouble)
                .collect(() -> new float[mapString.split(",").length],
                        (arr, val) -> arr[arr.length - 1] = (float) val,
                        (arr1, arr2) -> {});
    }

    @Override
    public void prepare(Progress progress) {}

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends BaseBuilder<Builder> {
        List<CSVRecord> csvRecords;

        public Builder() {}

        @Override
        protected Builder self() {
            return this;
        }

        public CSVDataset build(String nomFichier) throws IOException {
            try (Reader reader = Files.newBufferedReader(Paths.get(nomFichier));
                 CSVParser csvParser =
                         new CSVParser(reader, CSVFormat.DEFAULT
                                 .withHeader("map", "dep")
                                 .withFirstRecordAsHeader()
                                 .withIgnoreHeaderCase()
                                 .withTrim())) {
                csvRecords = csvParser.getRecords();
            }
            return new CSVDataset(this);
        }
    }
}