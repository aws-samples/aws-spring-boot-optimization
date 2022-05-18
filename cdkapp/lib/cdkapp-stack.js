"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.CdkappStack = void 0;
const aws_cdk_lib_1 = require("aws-cdk-lib");
const aws_cdk_lib_2 = require("aws-cdk-lib");
const aws_cdk_lib_3 = require("aws-cdk-lib");
const aws_cdk_lib_4 = require("aws-cdk-lib");
const aws_cdk_lib_5 = require("aws-cdk-lib");
const aws_cdk_lib_6 = require("aws-cdk-lib");
class CdkappStack extends aws_cdk_lib_1.Stack {
    constructor(scope, id, props) {
        super(scope, id, props);
        const containerImage = new aws_cdk_lib_1.CfnParameter(this, 'containerImage', {
            type: 'String',
            description: 'The name of the container image to deploy',
        });
        console.log('containerImage 👉 ', containerImage.valueAsString);
        const vpc = new aws_cdk_lib_2.aws_ec2.Vpc(this, "spring-demo", {
            maxAzs: 3
        });
        const table = new aws_cdk_lib_5.aws_dynamodb.Table(this, "Customers", {
            partitionKey: { name: "id", type: aws_cdk_lib_5.aws_dynamodb.AttributeType.STRING, },
            tableName: "Customers",
            readCapacity: 50,
            writeCapacity: 50,
            removalPolicy: aws_cdk_lib_1.RemovalPolicy.DESTROY,
        });
        const cluster = new aws_cdk_lib_3.aws_ecs.Cluster(this, "spring-demo-cluster", {
            vpc: vpc
        });
        const logging = new aws_cdk_lib_3.aws_ecs.AwsLogDriver({
            streamPrefix: "quarkus-demo"
        });
        const taskRole = new aws_cdk_lib_6.aws_iam.Role(this, "spring-demo-taskRole", {
            roleName: "quarkus-demo-taskRole",
            assumedBy: new aws_cdk_lib_6.aws_iam.ServicePrincipal("ecs-tasks.amazonaws.com")
        });
        const taskDef = new aws_cdk_lib_3.aws_ecs.FargateTaskDefinition(this, "spring-demo-taskdef", {
            taskRole: taskRole
        });
        const container = taskDef.addContainer("spring-demo-web", {
            image: aws_cdk_lib_3.aws_ecs.ContainerImage.fromRegistry(containerImage.valueAsString),
            memoryLimitMiB: 256,
            cpu: 256,
            logging
        });
        container.addPortMappings({
            containerPort: 8080,
            hostPort: 8080,
            protocol: aws_cdk_lib_3.aws_ecs.Protocol.TCP
        });
        const fargateService = new aws_cdk_lib_4.aws_ecs_patterns.ApplicationLoadBalancedFargateService(this, "spring-demo-service", {
            cluster: cluster,
            taskDefinition: taskDef,
            publicLoadBalancer: true,
            desiredCount: 3,
            listenerPort: 8080
        });
        const scaling = fargateService.service.autoScaleTaskCount({ maxCapacity: 6 });
        scaling.scaleOnCpuUtilization("CpuScaling", {
            targetUtilizationPercent: 10,
            scaleInCooldown: aws_cdk_lib_1.Duration.seconds(60),
            scaleOutCooldown: aws_cdk_lib_1.Duration.seconds(60)
        });
        table.grantReadWriteData(taskRole);
        new aws_cdk_lib_1.CfnOutput(this, "LoadBalancerDNS", { value: fargateService.loadBalancer.loadBalancerDnsName });
    }
}
exports.CdkappStack = CdkappStack;
const app = new aws_cdk_lib_1.App();
new CdkappStack(app, "CdkappStack", {
    env: {
        region: "eu-west-1",
        account: process.env.CDK_DEFAULT_ACCOUNT,
    }
});
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY2RrYXBwLXN0YWNrLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiY2RrYXBwLXN0YWNrLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUFBLDZDQUF1RztBQUd2Ryw2Q0FBNkM7QUFDN0MsNkNBQTZDO0FBQzdDLDZDQUErRDtBQUMvRCw2Q0FBdUQ7QUFDdkQsNkNBQTZDO0FBRTdDLE1BQWEsV0FBWSxTQUFRLG1CQUFLO0lBQ3BDLFlBQVksS0FBZ0IsRUFBRSxFQUFVLEVBQUUsS0FBa0I7UUFDMUQsS0FBSyxDQUFDLEtBQUssRUFBRSxFQUFFLEVBQUUsS0FBSyxDQUFDLENBQUM7UUFFeEIsTUFBTSxjQUFjLEdBQUcsSUFBSSwwQkFBWSxDQUFDLElBQUksRUFBRSxnQkFBZ0IsRUFBRTtZQUM5RCxJQUFJLEVBQUUsUUFBUTtZQUNkLFdBQVcsRUFBRSwyQ0FBMkM7U0FDekQsQ0FBQyxDQUFDO1FBRUgsT0FBTyxDQUFDLEdBQUcsQ0FBQyxvQkFBb0IsRUFBRSxjQUFjLENBQUMsYUFBYSxDQUFDLENBQUM7UUFFaEUsTUFBTSxHQUFHLEdBQUcsSUFBSSxxQkFBRyxDQUFDLEdBQUcsQ0FBQyxJQUFJLEVBQUUsYUFBYSxFQUFFO1lBQzNDLE1BQU0sRUFBRSxDQUFDO1NBQ1YsQ0FBQyxDQUFDO1FBRUgsTUFBTSxLQUFLLEdBQUcsSUFBSSwwQkFBUSxDQUFDLEtBQUssQ0FBQyxJQUFJLEVBQUUsV0FBVyxFQUFFO1lBQ2xELFlBQVksRUFBRSxFQUFFLElBQUksRUFBRSxJQUFJLEVBQUUsSUFBSSxFQUFFLDBCQUFRLENBQUMsYUFBYSxDQUFDLE1BQU0sR0FBRztZQUNsRSxTQUFTLEVBQUUsV0FBVztZQUN0QixZQUFZLEVBQUUsRUFBRTtZQUNoQixhQUFhLEVBQUUsRUFBRTtZQUNqQixhQUFhLEVBQUUsMkJBQWEsQ0FBQyxPQUFPO1NBQ3JDLENBQUMsQ0FBQztRQUVILE1BQU0sT0FBTyxHQUFHLElBQUkscUJBQUcsQ0FBQyxPQUFPLENBQUMsSUFBSSxFQUFFLHFCQUFxQixFQUFFO1lBQzNELEdBQUcsRUFBRSxHQUFHO1NBQ1QsQ0FBQyxDQUFDO1FBRUgsTUFBTSxPQUFPLEdBQUcsSUFBSSxxQkFBRyxDQUFDLFlBQVksQ0FBQztZQUNuQyxZQUFZLEVBQUUsY0FBYztTQUM3QixDQUFDLENBQUE7UUFFRixNQUFNLFFBQVEsR0FBRyxJQUFJLHFCQUFHLENBQUMsSUFBSSxDQUFDLElBQUksRUFBRSxzQkFBc0IsRUFBRTtZQUMxRCxRQUFRLEVBQUUsdUJBQXVCO1lBQ2pDLFNBQVMsRUFBRSxJQUFJLHFCQUFHLENBQUMsZ0JBQWdCLENBQUMseUJBQXlCLENBQUM7U0FDL0QsQ0FBQyxDQUFDO1FBRUgsTUFBTSxPQUFPLEdBQUcsSUFBSSxxQkFBRyxDQUFDLHFCQUFxQixDQUFDLElBQUksRUFBRSxxQkFBcUIsRUFBRTtZQUN6RSxRQUFRLEVBQUUsUUFBUTtTQUNuQixDQUFDLENBQUM7UUFFSCxNQUFNLFNBQVMsR0FBRyxPQUFPLENBQUMsWUFBWSxDQUFDLGlCQUFpQixFQUFFO1lBQ3hELEtBQUssRUFBRSxxQkFBRyxDQUFDLGNBQWMsQ0FBQyxZQUFZLENBQUMsY0FBYyxDQUFDLGFBQWEsQ0FBQztZQUNwRSxjQUFjLEVBQUUsR0FBRztZQUNuQixHQUFHLEVBQUUsR0FBRztZQUNSLE9BQU87U0FDUixDQUFDLENBQUM7UUFFSCxTQUFTLENBQUMsZUFBZSxDQUFDO1lBQ3hCLGFBQWEsRUFBRSxJQUFJO1lBQ25CLFFBQVEsRUFBRSxJQUFJO1lBQ2QsUUFBUSxFQUFFLHFCQUFHLENBQUMsUUFBUSxDQUFDLEdBQUc7U0FDM0IsQ0FBQyxDQUFDO1FBRUgsTUFBTSxjQUFjLEdBQUcsSUFBSSw4QkFBWSxDQUFDLHFDQUFxQyxDQUFDLElBQUksRUFBRSxxQkFBcUIsRUFBRTtZQUN6RyxPQUFPLEVBQUUsT0FBTztZQUNoQixjQUFjLEVBQUUsT0FBTztZQUN2QixrQkFBa0IsRUFBRSxJQUFJO1lBQ3hCLFlBQVksRUFBRSxDQUFDO1lBQ2YsWUFBWSxFQUFFLElBQUk7U0FDbkIsQ0FBQyxDQUFDO1FBRUgsTUFBTSxPQUFPLEdBQUcsY0FBYyxDQUFDLE9BQU8sQ0FBQyxrQkFBa0IsQ0FBQyxFQUFFLFdBQVcsRUFBRSxDQUFDLEVBQUUsQ0FBQyxDQUFDO1FBQzlFLE9BQU8sQ0FBQyxxQkFBcUIsQ0FBQyxZQUFZLEVBQUU7WUFDMUMsd0JBQXdCLEVBQUUsRUFBRTtZQUM1QixlQUFlLEVBQUUsc0JBQVEsQ0FBQyxPQUFPLENBQUMsRUFBRSxDQUFDO1lBQ3JDLGdCQUFnQixFQUFFLHNCQUFRLENBQUMsT0FBTyxDQUFDLEVBQUUsQ0FBQztTQUN2QyxDQUFDLENBQUM7UUFFSCxLQUFLLENBQUMsa0JBQWtCLENBQUMsUUFBUSxDQUFDLENBQUE7UUFFbEMsSUFBSSx1QkFBUyxDQUFDLElBQUksRUFBRSxpQkFBaUIsRUFBRSxFQUFFLEtBQUssRUFBRSxjQUFjLENBQUMsWUFBWSxDQUFDLG1CQUFtQixFQUFFLENBQUMsQ0FBQztJQUNyRyxDQUFDO0NBQ0Y7QUF4RUQsa0NBd0VDO0FBRUQsTUFBTSxHQUFHLEdBQUcsSUFBSSxpQkFBRyxFQUFFLENBQUM7QUFFdEIsSUFBSSxXQUFXLENBQUMsR0FBRyxFQUFFLGFBQWEsRUFBRTtJQUNsQyxHQUFHLEVBQUU7UUFDSCxNQUFNLEVBQUUsV0FBVztRQUNuQixPQUFPLEVBQUUsT0FBTyxDQUFDLEdBQUcsQ0FBQyxtQkFBbUI7S0FDekM7Q0FDRixDQUFDLENBQUMiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBTdGFjaywgU3RhY2tQcm9wcywgUmVtb3ZhbFBvbGljeSwgQ2ZuT3V0cHV0LCBEdXJhdGlvbiwgQXBwLCBDZm5QYXJhbWV0ZXIgfSBmcm9tICdhd3MtY2RrLWxpYic7XG5pbXBvcnQgeyBDb25zdHJ1Y3QgfSBmcm9tICdjb25zdHJ1Y3RzJztcblxuaW1wb3J0IHsgYXdzX2VjMiBhcyBlYzIgfSBmcm9tICdhd3MtY2RrLWxpYic7XG5pbXBvcnQgeyBhd3NfZWNzIGFzIGVjcyB9IGZyb20gJ2F3cy1jZGstbGliJztcbmltcG9ydCB7IGF3c19lY3NfcGF0dGVybnMgYXMgZWNzX3BhdHRlcm5zIH0gZnJvbSAnYXdzLWNkay1saWInO1xuaW1wb3J0IHsgYXdzX2R5bmFtb2RiIGFzIGR5bmFtb2RiIH0gZnJvbSAnYXdzLWNkay1saWInO1xuaW1wb3J0IHsgYXdzX2lhbSBhcyBpYW0gfSBmcm9tICdhd3MtY2RrLWxpYic7XG5cbmV4cG9ydCBjbGFzcyBDZGthcHBTdGFjayBleHRlbmRzIFN0YWNrIHtcbiAgY29uc3RydWN0b3Ioc2NvcGU6IENvbnN0cnVjdCwgaWQ6IHN0cmluZywgcHJvcHM/OiBTdGFja1Byb3BzKSB7XG4gICAgc3VwZXIoc2NvcGUsIGlkLCBwcm9wcyk7XG5cbiAgICBjb25zdCBjb250YWluZXJJbWFnZSA9IG5ldyBDZm5QYXJhbWV0ZXIodGhpcywgJ2NvbnRhaW5lckltYWdlJywge1xuICAgICAgdHlwZTogJ1N0cmluZycsXG4gICAgICBkZXNjcmlwdGlvbjogJ1RoZSBuYW1lIG9mIHRoZSBjb250YWluZXIgaW1hZ2UgdG8gZGVwbG95JyxcbiAgICB9KTtcbiAgICBcbiAgICBjb25zb2xlLmxvZygnY29udGFpbmVySW1hZ2Ug8J+RiSAnLCBjb250YWluZXJJbWFnZS52YWx1ZUFzU3RyaW5nKTtcblxuICAgIGNvbnN0IHZwYyA9IG5ldyBlYzIuVnBjKHRoaXMsIFwic3ByaW5nLWRlbW9cIiwge1xuICAgICAgbWF4QXpzOiAzXG4gICAgfSk7XG5cbiAgICBjb25zdCB0YWJsZSA9IG5ldyBkeW5hbW9kYi5UYWJsZSh0aGlzLCBcIkN1c3RvbWVyc1wiLCB7XG4gICAgICBwYXJ0aXRpb25LZXk6IHsgbmFtZTogXCJpZFwiLCB0eXBlOiBkeW5hbW9kYi5BdHRyaWJ1dGVUeXBlLlNUUklORywgfSxcbiAgICAgIHRhYmxlTmFtZTogXCJDdXN0b21lcnNcIixcbiAgICAgIHJlYWRDYXBhY2l0eTogNTAsXG4gICAgICB3cml0ZUNhcGFjaXR5OiA1MCxcbiAgICAgIHJlbW92YWxQb2xpY3k6IFJlbW92YWxQb2xpY3kuREVTVFJPWSwgLy8gTk9UIHJlY29tbWVuZGVkIGZvciBwcm9kdWN0aW9uIGNvZGVcbiAgICB9KTtcblxuICAgIGNvbnN0IGNsdXN0ZXIgPSBuZXcgZWNzLkNsdXN0ZXIodGhpcywgXCJzcHJpbmctZGVtby1jbHVzdGVyXCIsIHtcbiAgICAgIHZwYzogdnBjXG4gICAgfSk7XG5cbiAgICBjb25zdCBsb2dnaW5nID0gbmV3IGVjcy5Bd3NMb2dEcml2ZXIoe1xuICAgICAgc3RyZWFtUHJlZml4OiBcInF1YXJrdXMtZGVtb1wiXG4gICAgfSlcblxuICAgIGNvbnN0IHRhc2tSb2xlID0gbmV3IGlhbS5Sb2xlKHRoaXMsIFwic3ByaW5nLWRlbW8tdGFza1JvbGVcIiwge1xuICAgICAgcm9sZU5hbWU6IFwicXVhcmt1cy1kZW1vLXRhc2tSb2xlXCIsXG4gICAgICBhc3N1bWVkQnk6IG5ldyBpYW0uU2VydmljZVByaW5jaXBhbChcImVjcy10YXNrcy5hbWF6b25hd3MuY29tXCIpXG4gICAgfSk7XG5cbiAgICBjb25zdCB0YXNrRGVmID0gbmV3IGVjcy5GYXJnYXRlVGFza0RlZmluaXRpb24odGhpcywgXCJzcHJpbmctZGVtby10YXNrZGVmXCIsIHtcbiAgICAgIHRhc2tSb2xlOiB0YXNrUm9sZVxuICAgIH0pO1xuXG4gICAgY29uc3QgY29udGFpbmVyID0gdGFza0RlZi5hZGRDb250YWluZXIoXCJzcHJpbmctZGVtby13ZWJcIiwge1xuICAgICAgaW1hZ2U6IGVjcy5Db250YWluZXJJbWFnZS5mcm9tUmVnaXN0cnkoY29udGFpbmVySW1hZ2UudmFsdWVBc1N0cmluZyksXG4gICAgICBtZW1vcnlMaW1pdE1pQjogMjU2LFxuICAgICAgY3B1OiAyNTYsXG4gICAgICBsb2dnaW5nXG4gICAgfSk7XG5cbiAgICBjb250YWluZXIuYWRkUG9ydE1hcHBpbmdzKHtcbiAgICAgIGNvbnRhaW5lclBvcnQ6IDgwODAsXG4gICAgICBob3N0UG9ydDogODA4MCxcbiAgICAgIHByb3RvY29sOiBlY3MuUHJvdG9jb2wuVENQXG4gICAgfSk7XG5cbiAgICBjb25zdCBmYXJnYXRlU2VydmljZSA9IG5ldyBlY3NfcGF0dGVybnMuQXBwbGljYXRpb25Mb2FkQmFsYW5jZWRGYXJnYXRlU2VydmljZSh0aGlzLCBcInNwcmluZy1kZW1vLXNlcnZpY2VcIiwge1xuICAgICAgY2x1c3RlcjogY2x1c3RlcixcbiAgICAgIHRhc2tEZWZpbml0aW9uOiB0YXNrRGVmLFxuICAgICAgcHVibGljTG9hZEJhbGFuY2VyOiB0cnVlLFxuICAgICAgZGVzaXJlZENvdW50OiAzLFxuICAgICAgbGlzdGVuZXJQb3J0OiA4MDgwXG4gICAgfSk7XG5cbiAgICBjb25zdCBzY2FsaW5nID0gZmFyZ2F0ZVNlcnZpY2Uuc2VydmljZS5hdXRvU2NhbGVUYXNrQ291bnQoeyBtYXhDYXBhY2l0eTogNiB9KTtcbiAgICBzY2FsaW5nLnNjYWxlT25DcHVVdGlsaXphdGlvbihcIkNwdVNjYWxpbmdcIiwge1xuICAgICAgdGFyZ2V0VXRpbGl6YXRpb25QZXJjZW50OiAxMCxcbiAgICAgIHNjYWxlSW5Db29sZG93bjogRHVyYXRpb24uc2Vjb25kcyg2MCksXG4gICAgICBzY2FsZU91dENvb2xkb3duOiBEdXJhdGlvbi5zZWNvbmRzKDYwKVxuICAgIH0pO1xuXG4gICAgdGFibGUuZ3JhbnRSZWFkV3JpdGVEYXRhKHRhc2tSb2xlKVxuXG4gICAgbmV3IENmbk91dHB1dCh0aGlzLCBcIkxvYWRCYWxhbmNlckROU1wiLCB7IHZhbHVlOiBmYXJnYXRlU2VydmljZS5sb2FkQmFsYW5jZXIubG9hZEJhbGFuY2VyRG5zTmFtZSB9KTtcbiAgfVxufVxuICBcbmNvbnN0IGFwcCA9IG5ldyBBcHAoKTtcblxubmV3IENka2FwcFN0YWNrKGFwcCwgXCJDZGthcHBTdGFja1wiLCB7XG4gIGVudjoge1xuICAgIHJlZ2lvbjogXCJldS13ZXN0LTFcIixcbiAgICBhY2NvdW50OiBwcm9jZXNzLmVudi5DREtfREVGQVVMVF9BQ0NPVU5ULFxuICB9XG59KTsiXX0=