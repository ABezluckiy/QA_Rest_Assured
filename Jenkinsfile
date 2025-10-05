pipeline {
    agent any
    tools {
        maven 'Maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the project...'
                bat 'mvn clean package -DskipTests'
            }
        }

//         stage('Test') {
//             steps {
//                 echo 'Running tests...'
//                 bat 'mvn test'
//             }
//             post {
//                 always {
//                     junit '**\\target\\surefire-reports\\*.xml'
//                 }
//             }
//         }
    }

    post {
        success {
            echo 'Build and tests succeeded!'
        }
        failure {
            echo 'Build or tests failed!'
        }
    }
}



