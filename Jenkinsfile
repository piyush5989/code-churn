library 'cdd-global-pipeline-libraries'

pipeline {
 agent {
  label 'master'
 }
 stages {
  stage('Checkout') {
   steps {
    echo "***Build***"
   }
  }
 }
 post {
  success {
   sendNotificationToDevOpsCall()
  }
 }
}
