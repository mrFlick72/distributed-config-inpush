import boto3
import uuid
import json


def lambda_handler(event, context):
    kinesis = boto3.client("kinesis")
    s3 = boto3.resource('s3')

    bucket = event['Records'][0]['s3']['bucket']['name']
    objKey = event['Records'][0]['s3']['object']['key']
    obj = s3.Object(bucket, objKey)
    content = obj.get()['Body'].read().decode('utf-8')
    envelope = {"id": f"{bucket}/{objKey}", "content": content}
    kinesis.put_record(Data=json.dumps(envelope), PartitionKey=str(uuid.uuid4()), StreamName='xxxxx')

    return {
        'statusCode': 200
    }
