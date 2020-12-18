import json
import boto3
import uuid


if __name__ == '__main__':
    kinesis = boto3.client("kinesis")
    s3 = boto3.resource('s3')

    obj = s3.Object('distributed-config-inpush-eu-central-1', 'components.yaml')
    content = obj.get()['Body'].read().decode('utf-8')
    print(content)

    result = kinesis.put_record(Data=content, PartitionKey=str(uuid.uuid4()), StreamName='distributed-config-inpush-stream')

    print(result)

