#!/usr/bin/env bash
# Creates the DynamoDB tables needed for local development.
# Requires AWS CLI. DynamoDB Local must be running on port 8000.

set -euo pipefail

ENDPOINT="http://localhost:8001"
REGION="us-east-1"
AWS="aws --endpoint-url $ENDPOINT --region $REGION"

echo "Creating Recipes table..."
$AWS dynamodb create-table \
  --table-name Recipes \
  --attribute-definitions \
    AttributeName=id,AttributeType=S \
    AttributeName=url,AttributeType=S \
  --key-schema \
    AttributeName=id,KeyType=HASH \
  --global-secondary-indexes '[
    {
      "IndexName": "urlIndex",
      "KeySchema": [{"AttributeName": "url", "KeyType": "HASH"}],
      "Projection": {"ProjectionType": "ALL"},
      "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5}
    }
  ]' \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

echo "Creating UserSavedRecipes table..."
$AWS dynamodb create-table \
  --table-name UserSavedRecipes \
  --attribute-definitions \
    AttributeName=userId,AttributeType=S \
    AttributeName=listId,AttributeType=S \
  --key-schema \
    AttributeName=userId,KeyType=HASH \
    AttributeName=listId,KeyType=RANGE \
  --global-secondary-indexes '[
    {
      "IndexName": "listIdIndex",
      "KeySchema": [{"AttributeName": "listId", "KeyType": "HASH"}],
      "Projection": {"ProjectionType": "ALL"},
      "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5}
    }
  ]' \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

echo "Done. Tables created:"
$AWS dynamodb list-tables
