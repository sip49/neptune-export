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

package com.amazonaws.services.neptune.export;

import com.amazonaws.services.neptune.propertygraph.ExportStats;
import com.amazonaws.services.neptune.propertygraph.schema.GraphSchema;

import java.nio.file.Path;

public interface NeptuneExportEventHandler {

    NeptuneExportEventHandler NULL_EVENT_HANDLER = new NeptuneExportEventHandler() {

        @Override
        public void onExportComplete(Path outputPath, ExportStats stats) throws Exception {
            //Do nothing
        }

        @Override
        public void onExportComplete(Path outputPath, ExportStats stats, GraphSchema graphSchema) throws Exception {
            //Do nothing
        }
    };

    void onExportComplete(Path outputPath, ExportStats stats) throws Exception;

    void onExportComplete(Path outputPath, ExportStats stats, GraphSchema graphSchema) throws Exception;
}
