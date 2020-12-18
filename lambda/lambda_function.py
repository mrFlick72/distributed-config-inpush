import boto3
import uuid


def lambda_handler(event, context):
    kinesis = boto3.client("kinesis")
    s3 = boto3.resource('s3')

    obj = s3.Object(event['Records'][0]['s3']['bucket']['name'], event['Records'][0]['s3']['object']['key'])
    content = obj.get()['Body'].read().decode('utf-8')

    kinesis.put_record(Data=content, PartitionKey=str(uuid.uuid4()), StreamName='xxxxx')

    return {
        'statusCode': 200
    }
