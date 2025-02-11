# Amazon Neptune Export CHANGELOG

## Neptune Export v1.1.0 (Release Date: TBD):

### Bug Fixes:

### New Features and Improvements:

## Neptune Export v1.0.7 (Release Date: September 27, 2023):

### New Features and Improvements:

- Cross Account Exports: New CLI options and parameters added to specify a role to assume when uploading to
Amazon S3 buckets or Amazon Kinesis Data Streams
- New --credentials-profile CLI option to fetch AWS Credentials from non-default AWS CLI profiles

## Neptune Export v1.0.6 (Release Date: July 31, 2023):

### Bug Fixes:

- Fixed bug which could lead to corrupted output in highly concurrent csv exports

## Neptune Export v1.0.5 (Release Date: June 5, 2023):

### Bug Fixes:

- Resolves issue which caused the error `gremlin-groovy is not an available GremlinScriptEngine` to appear when using the `--gremlin-filter` option in the uber jar.

## Neptune Export v1.0.4 (Release Date: May 31, 2023):

### New Features and Improvements:

- Upgraded to Gremlin Dependency Version to 3.6.2

## Neptune Export v1.0.3 (Release Date: April 27, 2023):

### Bug Fixes:

- Fixed NullPointerException bug during RDF Exports

### New Features and Improvements:

- Added aws-java-sdk-sts as a dependency to enable WebIdentityTokenFileCredentialsProvider.

## Neptune Export v1.0.2 (Release Date: April 7, 2023):

### Bug Fixes:

- Fixed bug which was preventing getting the output id in FileToStreamOutputWriter

### New Features and Improvements:

## Neptune Export v1.0.1 (Release Date: March 30, 2023):

### Bug Fixes:

- Updated NOTICE file in shaded jar

### New Features and Improvements:

- Added new `--disable-stream-aggregation` option for property graph exports to Kinesis streams. More details can be found [here](https://github.com/aws/neptune-export#exporting-to-an-amazon-kinesis-data-stream).

- Improved error messages from server side errors (such as timeout exceptions) for RDF exports.

## Neptune Export v1.0.0 (Release Date: February 28, 2023):

Neptune Export is a tool to perform bulk data exports from AWS Neptune. Neptune Export is migrated from the AWS Labs [Amazon Neptune Tools](https://github.com/awslabs/amazon-neptune-tools) repository, and the old module is now deprecated. In this release, the release artifact `neptune-export.jar` has been renamed to `neptune-export-1.0.0-all.jar`. Going forward, Neptune Export will be following this new versioned naming scheme.

Instructions for running export jobs can be found in the docs/ directory.

A few changes are included since the migration. 

### Bug Fixes:

- Corrected r6g instance type prefix (used to be listed as r6d).

### New Features and Improvements:

- Added a new optional parameter to use customer managed KMS key for S3 server-side encryption.
- Added integration tests for developers requiring manual setup (see docs/dev/IntegrationTests.md).