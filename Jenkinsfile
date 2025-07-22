pipeline {
    agent any

    tools {
        maven 'Maven 3.8.6'
        jdk 'JDK 21'
    }

    stages {
        stage('Checkout') {
            steps {
                // Скачиваем код из SCM (Git)
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the project...'
                // Собираем проект, пропуская тесты на этом шаге
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                // Запускаем тесты с Maven
                sh 'mvn test'
            }
            post {
                always {
                    // Публикуем результаты тестов в Jenkins
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

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
