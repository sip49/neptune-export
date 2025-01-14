package com.amazonaws.services.neptune.propertygraph.io;

import com.amazonaws.services.neptune.io.Directories;
import com.amazonaws.services.neptune.io.Status;
import com.amazonaws.services.neptune.propertygraph.GraphClient;
import com.amazonaws.services.neptune.propertygraph.Label;
import com.amazonaws.services.neptune.propertygraph.LabelsFilter;
import com.amazonaws.services.neptune.propertygraph.io.result.PGResult;
import com.amazonaws.services.neptune.propertygraph.schema.FileSpecificLabelSchemas;
import com.amazonaws.services.neptune.propertygraph.schema.GraphElementSchemas;
import com.amazonaws.services.neptune.propertygraph.schema.LabelSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class ExportPGTaskHandler<T extends PGResult> implements GraphElementHandler<T> {

    private static final Logger logger = LoggerFactory.getLogger(ExportPGTaskHandler.class);

    private final FileSpecificLabelSchemas fileSpecificLabelSchemas;
    private final GraphElementSchemas graphElementSchemas;
    private final PropertyGraphTargetConfig targetConfig;
    private final WriterFactory<T> writerFactory;
    private final LabelWriters<T> labelWriters;
    private final GraphClient<T> graphClient;
    private final Status status;
    private final AtomicInteger index;

    private final LabelsFilter labelsFilter;

    ExportPGTaskHandler(FileSpecificLabelSchemas fileSpecificLabelSchemas,
                        GraphElementSchemas graphElementSchemas,
                        PropertyGraphTargetConfig targetConfig,
                        WriterFactory<T> writerFactory,
                        LabelWriters<T> labelWriters,
                        GraphClient<T> graphClient,
                        Status status,
                        AtomicInteger index,
                        LabelsFilter labelsFilter) {
        this.fileSpecificLabelSchemas = fileSpecificLabelSchemas;
        this.graphElementSchemas = graphElementSchemas;
        this.targetConfig = targetConfig;
        this.writerFactory = writerFactory;
        this.labelWriters = labelWriters;
        this.graphClient = graphClient;
        this.status = status;
        this.index = index;
        this.labelsFilter = labelsFilter;
    }

    @Override
    public void handle(T input, boolean allowTokens) throws IOException {
        status.update();
        Label label = labelsFilter.getLabelFor(input);
        if (!labelWriters.containsKey(label)) {
            createWriterFor(label);
        }
        if(graphClient != null) {
            graphClient.updateStats(label);
        }
        labelWriters.get(label).handle(input, allowTokens);
    }

    @Override
    public void close() {
        try {
            labelWriters.close();
        } catch (Exception e) {
            logger.warn("Error closing label writer: {}.", e.getMessage());
        }
    }

    private void createWriterFor(Label label) {
        try {
            LabelSchema labelSchema = graphElementSchemas.getSchemaFor(label);

            PropertyGraphPrinter propertyGraphPrinter = writerFactory.createPrinter(
                    Directories.fileName(label.fullyQualifiedLabel(), index),
                    labelSchema,
                    targetConfig);
            LabelWriter<T> labelWriter = writerFactory.createLabelWriter(propertyGraphPrinter, labelSchema.label());

            labelWriters.put(label, labelWriter);
            fileSpecificLabelSchemas.add(labelWriter.outputId(), targetConfig.format(), labelSchema);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
