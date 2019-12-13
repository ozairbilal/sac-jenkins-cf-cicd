//Picking a build agent labeled "ec2" to run pipeline on
//node ('ec2'){
pipeline {
    agent any
    options { skipStagesAfterUnstable() }
stages{
  stage ('Pull from SCM'){
    steps{
      //Passing the pipeline the ID of my GitHub credentials and specifying the repo for my app
        git credentialsId: '32f2c3c2-c19e-431a-b421-a4376fce1186', url: 'https://github.com/ozairbilal/game-of-life.git'
      }
  }  
  stage('Build app' ){
    steps{
        sh 'mvn install'
        //Running the maven build and archiving the war
        sh 'mvn install'
        archive 'target/*.war'
    }
  }
stage('Package Image Push Image'){
  steps{
        //Packaging the image into a Docker image
   //     def pkg = docker.build ('lavaliere/game-of-life', '.')
        //Pushing the packaged app in image into DockerHub
     /*   docker.withRegistry ('https://index.docker.io/v1/', 'ed17cd18-975e-4224-a231-014ecd23942b') {
        sh 'ls -lart' 
        pkg.push 'docker-demo'
        }*/
      echo 'Build docker image game-of-life...'
      sh 'docker build -t 564007293907.dkr.ecr.us-east-1.amazonaws.com/game-of-life:latest gameoflife-web'
      echo 'Push docker image game-of-life to ECR...'
      docker.withRegistry('https://564007293907.dkr.ecr.us-east-1.amazonaws.com', 'ecr:aws') {
      sh 'docker push 564007293907.dkr.ecr.us-east-1.amazonaws.com/game-of-life:latest'
  }
}
stage ('Stage image'){
    steps{
        //Deploy image to staging in ECS
      def buildenv = docker.image('cloudbees/java-build-tools:0.0.7.1')
      buildenv.inside {
        wrap([$class: 'AmazonAwsCliBuildWrapper', credentialsId: '20f6b2e4-7fbe-4655-8b4b-9842ec81bce2', defaultRegion: 'us-east-1']) {
            sh "aws ecs update-service --service staging-game  --cluster staging --desired-count 0"
            timeout(time: 5, unit: 'MINUTES') {
                waitUntil {
                    sh "aws ecs describe-services --services staging-game  --cluster staging  > .amazon-ecs-service-status.json"

                    // parse `describe-services` output
                    def ecsServicesStatusAsJson = readFile(".amazon-ecs-service-status.json")
                    def ecsServicesStatus = new groovy.json.JsonSlurper().parseText(ecsServicesStatusAsJson)
                    println "$ecsServicesStatus"
                    def ecsServiceStatus = ecsServicesStatus.services[0]
                    return ecsServiceStatus.get('runningCount') == 0 && ecsServiceStatus.get('status') == "ACTIVE"
                }
            }
            sh "aws ecs update-service --service staging-game  --cluster staging --desired-count 1"
            timeout(time: 5, unit: 'MINUTES') {
                waitUntil {
                    sh "aws ecs describe-services --services staging-game --cluster staging > .amazon-ecs-service-status.json"

                    // parse `describe-services` output
                    def ecsServicesStatusAsJson = readFile(".amazon-ecs-service-status.json")
                    def ecsServicesStatus = new groovy.json.JsonSlurper().parseText(ecsServicesStatusAsJson)
                    println "$ecsServicesStatus"
                    def ecsServiceStatus = ecsServicesStatus.services[0]
                    return ecsServiceStatus.get('runningCount') == 0 && ecsServiceStatus.get('status') == "ACTIVE"
                }
            }
            timeout(time: 5, unit: 'MINUTES') {
                waitUntil {
                    try {
                        sh "curl http://52.200.92.100:80"
                        return true
                    } catch (Exception e) {
                        return false
                    }
                }
            }
            echo "gameoflife#${env.BUILD_NUMBER} SUCCESSFULLY deployed to http://52.200.92.100:80"
            input 'Does staging http://52.200.92.100:80 look okay?'
        }
      }
    }
  }
}
}
