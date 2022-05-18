import { Stack, StackProps, RemovalPolicy, CfnOutput, Duration, App, CfnParameter } from 'aws-cdk-lib';
import { Construct } from 'constructs';

import { aws_ec2 as ec2 } from 'aws-cdk-lib';
import { aws_ecs as ecs } from 'aws-cdk-lib';
import { aws_s3 as s3 } from 'aws-cdk-lib';
import { aws_kinesisfirehose as kinesisfirehose } from 'aws-cdk-lib';
import { aws_ecs_patterns as ecs_patterns } from 'aws-cdk-lib';
import { aws_dynamodb as dynamodb } from 'aws-cdk-lib';
import { aws_iam as iam } from 'aws-cdk-lib';

export class CdkappStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const ExecutionRolePolicy = new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      resources: ['*'],
      actions: [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage",
        "logs:CreateLogGroup",
        "logs:DescribeLogStreams",
        "logs:CreateLogStream",
        "logs:DescribeLogGroups",
        "logs:PutLogEvents",
        "xray:PutTraceSegments",
        "xray:PutTelemetryRecords",
        "xray:GetSamplingRules",
        "xray:GetSamplingTargets",
        "xray:GetSamplingStatisticSummaries",
        'ssm:GetParameters'
      ]
    });

    const TaskRolePolicy = new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      resources: ['*'],
      actions: [
        "ecs:DescribeTasks",
        "ecs:ListTasks"
        ]
    });

    const containerImage = new CfnParameter(this, 'containerImage', {
      type: 'String',
      description: 'The name of the container image to deploy',
    });
    
    const cpuType = this.node.tryGetContext('cpuType');

    const vpc = new ec2.Vpc(this, "spring-demo", {
      maxAzs: 3
    });

    const table = new dynamodb.Table(this, "Customer", {
      partitionKey: { name: "Id", type: dynamodb.AttributeType.STRING, },
      tableName: "Customer",
      readCapacity: 50,
      writeCapacity: 50,
      removalPolicy: RemovalPolicy.DESTROY, // NOT recommended for production code
    });

    const bucket = new s3.Bucket(this, 'spring-demo-log-bucket');

    const cluster = new ecs.Cluster(this, "spring-demo-cluster", {
      vpc: vpc
    });

    const logging = new ecs.AwsLogDriver({
      streamPrefix: "spring-demo"
    })

    const taskRole = new iam.Role(this, "spring-demo-taskRole", {
      roleName: "spring-demo-taskRole",
      assumedBy: new iam.ServicePrincipal("ecs-tasks.amazonaws.com")
    });
    
    taskRole.addToPolicy(TaskRolePolicy);

    console.log("cpuType: " + cpuType)

    const taskDef = new ecs.FargateTaskDefinition(this, "spring-demo-taskdef", {
      runtimePlatform: {
        operatingSystemFamily: ecs.OperatingSystemFamily.LINUX,
        cpuArchitecture: (cpuType === 'ARM64' ? ecs.CpuArchitecture.ARM64 : ecs.CpuArchitecture.X86_64),
      },
      taskRole: taskRole,
      cpu: 1024,
      memoryLimitMiB: 2048
    });  

    taskDef.addToExecutionRolePolicy(ExecutionRolePolicy);

    const container = taskDef.addContainer("spring-demo-web", {
      image: ecs.ContainerImage.fromRegistry(containerImage.valueAsString),
      memoryLimitMiB: 1024,
      cpu: 512,
      logging
    });

    container.addPortMappings({
      containerPort: 8080,
      hostPort: 8080,
      protocol: ecs.Protocol.TCP
    });

    const fargateService = new ecs_patterns.ApplicationLoadBalancedFargateService(this, "spring-demo-service", {
      cluster: cluster,
      taskDefinition: taskDef,
      publicLoadBalancer: true,
      desiredCount: 3,
      listenerPort: 8080
    });

    fargateService.targetGroup.configureHealthCheck({
      path: "/actuator/health"
    });

    const scaling = fargateService.service.autoScaleTaskCount({ maxCapacity: 6 });
    scaling.scaleOnCpuUtilization("CpuScaling", {
      targetUtilizationPercent: 10,
      scaleInCooldown: Duration.seconds(60),
      scaleOutCooldown: Duration.seconds(60)
    });

    table.grantReadWriteData(taskRole)

    new CfnOutput(this, "LoadBalancerDNS", { value: fargateService.loadBalancer.loadBalancerDnsName });
  }
}
  
const app = new App();

new CdkappStack(app, "CdkappStack", {
  env: {
    region: "eu-west-1",
    account: process.env.CDK_DEFAULT_ACCOUNT,
  }
});