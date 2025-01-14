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

package com.amazonaws.services.neptune.propertygraph.io;

import com.amazonaws.services.neptune.propertygraph.Label;
import com.amazonaws.services.neptune.propertygraph.io.result.PGEdgeResult;
import com.amazonaws.services.neptune.propertygraph.io.result.PGResult;
import com.amazonaws.services.neptune.propertygraph.schema.LabelSchema;

import java.io.IOException;
import java.util.Map;

public class EdgesWriterFactory implements WriterFactory<PGEdgeResult> {

    @Override
    public PropertyGraphPrinter createPrinter(String name, LabelSchema labelSchema, PropertyGraphTargetConfig targetConfig) throws IOException {
        PropertyGraphPrinter propertyGraphPrinter = targetConfig.createPrinterForEdges(name, labelSchema);

        if (labelSchema.label().hasFromAndToLabels()){
            propertyGraphPrinter.printHeaderMandatoryColumns("id", "label", "from", "to", "fromLabels", "toLabels");
        } else {
            propertyGraphPrinter.printHeaderMandatoryColumns("id", "label", "from", "to");
        }

        propertyGraphPrinter.printHeaderRemainingColumns(labelSchema.propertySchemas());

        return propertyGraphPrinter;
    }

    @Override
    public LabelWriter<PGEdgeResult> createLabelWriter(PropertyGraphPrinter propertyGraphPrinter, Label label) {
        return new EdgeWriter(propertyGraphPrinter, label);
    }
}
