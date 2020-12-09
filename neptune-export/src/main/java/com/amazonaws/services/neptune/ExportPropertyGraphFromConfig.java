/*
Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License.
A copy of the License is located at
    http://www.apache.org/licenses/LICENSE-2.0
or in the "license" file accompanying this file. This file is distributed
on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language governing
permissions and limitations under the License.
*/

package com.amazonaws.services.neptune;

import com.amazonaws.services.neptune.cli.*;
import com.amazonaws.services.neptune.cluster.ClusterStrategy;
import com.amazonaws.services.neptune.io.Directories;
import com.amazonaws.services.neptune.io.DirectoryStructure;
import com.amazonaws.services.neptune.propertygraph.ExportStats;
import com.amazonaws.services.neptune.propertygraph.NeptuneGremlinClient;
import com.amazonaws.services.neptune.propertygraph.io.ExportPropertyGraphJob;
import com.amazonaws.services.neptune.propertygraph.io.JsonResource;
import com.amazonaws.services.neptune.propertygraph.io.PropertyGraphTargetConfig;
import com.amazonaws.services.neptune.propertygraph.schema.ExportSpecification;
import com.amazonaws.services.neptune.propertygraph.schema.GraphSchema;
import com.amazonaws.services.neptune.util.CheckedActivity;
import com.amazonaws.services.neptune.util.Timer;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.help.Examples;
import com.github.rvesse.airline.annotations.restrictions.Once;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Collection;

@Examples(examples = {
        "bin/neptune-export.sh export-pg-from-config -e neptunedbcluster-xxxxxxxxxxxx.cluster-yyyyyyyyyyyy.us-east-1.neptune.amazonaws.com -c /home/ec2-user/config.json -d /home/ec2-user/output",
        "bin/neptune-export.sh export-pg-from-config -e neptunedbcluster-xxxxxxxxxxxx.cluster-yyyyyyyyyyyy.us-east-1.neptune.amazonaws.com -c /home/ec2-user/config.json -d /home/ec2-user/output --format json"
}, descriptions = {
        "Export data using the schema config in /home/ec2-user/config.json",
        "Export data as JSON using the schema config in /home/ec2-user/config.json"
})
@Command(name = "export-pg-from-config", description = "Export property graph from Neptune to CSV or JSON using an existing schema config file.")
public class ExportPropertyGraphFromConfig extends NeptuneExportBaseCommand implements Runnable {

    @Inject
    private CloneClusterModule cloneStrategy = new CloneClusterModule(awsCli);

    @Inject
    private CommonConnectionModule connection = new CommonConnectionModule(awsCli);

    @Inject
    private PropertyGraphScopeModule scope = new PropertyGraphScopeModule();

    @Inject
    private PropertyGraphTargetModule target = new PropertyGraphTargetModule(true);

    @Inject
    private PropertyGraphConcurrencyModule concurrency = new PropertyGraphConcurrencyModule();

    @Inject
    private PropertyGraphSerializationModule serialization = new PropertyGraphSerializationModule();

    @Inject
    private PropertyGraphRangeModule range = new PropertyGraphRangeModule();

    @Inject
    private ExportPropertyGraphFromConfigModule graphSchemaProvider = new ExportPropertyGraphFromConfigModule();

    @Inject
    private CsvPrinterOptionsModule printerOptions = new CsvPrinterOptionsModule();

    @Override
    public void run() {

        try {
            Timer.timedActivity("exporting property graph from config", (CheckedActivity.Runnable) () -> {
                try (ClusterStrategy clusterStrategy = cloneStrategy.cloneCluster(connection.config(), concurrency.config())) {

                    Directories directories = target.createDirectories(DirectoryStructure.PropertyGraph);
                    JsonResource<GraphSchema> configFileResource = directories.configFileResource();
                    PropertyGraphTargetConfig targetConfig = target.config(directories, printerOptions.config());
                    GraphSchema graphSchema = graphSchemaProvider.graphSchema();
                    ExportStats stats = new ExportStats();

                    Collection<ExportSpecification<?>> exportSpecifications = scope.exportSpecifications(graphSchema, stats, labModeFeatures());

                    try (NeptuneGremlinClient client = NeptuneGremlinClient.create(clusterStrategy, serialization.config());
                         GraphTraversalSource g = client.newTraversalSource()) {

                        ExportPropertyGraphJob exportJob = new ExportPropertyGraphJob(
                                exportSpecifications,
                                graphSchema,
                                g,
                                range.config(),
                                clusterStrategy.concurrencyConfig(),
                                targetConfig);
                        graphSchema = exportJob.execute();
                        configFileResource.save(graphSchema);
                    }

                    directories.writeRootDirectoryPathAsMessage(target.description(), target);
                    configFileResource.writeResourcePathAsMessage(target);

                    System.err.println();
                    System.err.println(stats.formatStats(graphSchema));

                    Path outputPath = directories.writeRootDirectoryPathAsReturnValue(target);
                    onExportComplete(outputPath, stats, graphSchema);
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }
}
