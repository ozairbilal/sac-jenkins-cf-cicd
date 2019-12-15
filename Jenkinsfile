pipeline {
    agent {
        label 'slave'
    }
    options { skipStagesAfterUnstable() }
stages{
    stage ('Pull from SCM'){
    steps{
      //Passing the pipeline the ID of my GitHub credentials and specifying the repo for my app
        git credentialsId: 'github', url: 'https://github.com/ozairbilal/game-of-life.git'
      }
  }  
    stage('Build app' ){
    steps{
        script{
            sh 'mvn install'
            //Running the maven build and archiving the war
            sh 'mvn install'
            archiveArtifacts 'gameoflife-web/target/*.war'
        }
    }
  }
    stage('Package & Push Image'){
        steps{
            script{
                echo 'Build docker image game-of-life...'
                sh 'docker build -t 684599054637.dkr.ecr.us-east-1.amazonaws.com/ecs-jenkins:latest gameoflife-web'
                echo 'Push docker image game-of-life to ECR...'
                docker.withRegistry('https://684599054637.dkr.ecr.us-east-1.amazonaws.com', 'ecr:us-east-1:aws-credentials') {
                sh 'docker push 684599054637.dkr.ecr.us-east-1.amazonaws.com/ecs-jenkins:latest'
                }
            }
        }
    }
    stage ('Deploy Image'){
    steps{
        script{
        //Deploy image to staging in ECS
        def buildenv = docker.image('cloudbees/java-build-tools:0.0.7.1')
        sh "aws ecs update-service --service game-of-life  --cluster gol --desired-count 0"
        }
    }
  }
}
}
    
