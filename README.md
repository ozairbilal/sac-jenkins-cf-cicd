# sac-jenkins-cf-cicd
Following are the scripts for an ECS based web application, to be deployed by jenkins.

> Jenkinsfile - pipeline project for sample application:
- **Jenkinsfile:** [https://github.com/ozairbilal/sac-jenkins-cf-cicd/blob/master/Jenkinsfile]
> CloudFormation Script to provision the jenkins master and slave seperately:
- **Jenkins Master:** [https://github.com/ozairbilal/sac-jenkins-cf-cicd/blob/master/simple-ec2-jenkins.yaml]
- **Jenkins Slave:** [https://github.com/ozairbilal/sac-jenkins-cf-cicd/blob/master/jenkins-slave-cf.yaml]
> CloudFormation script to provision the ECS Cluster for sample application deployment:
- **ECS Sample Application:** [https://github.com/ozairbilal/sac-jenkins-cf-cicd/blob/master/ecs-cf.yaml]

After provisioning the Environments and having all the plugins installed for jenkins create a sample jenkins pipeline. And with the given script in jenkinsfile:
1. Code will be built and tests will be executed.
2. Then docker image will be created and published to **ECR.**
3. After Publishing the docker image the pipeline will deploy the image on **ECS.**
