pipeline {
    agent any

     options {
        timestamps()
    }

    environment {
        IMAGE_NAME = "ghcr.io/masjusufrh/registration-service"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                url: 'https://github.com/kafka-lesson/registration-service.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG .'
            }
        }

        stage('Security Scan') {
            steps {
                sh '''
                trivy image --exit-code 1 --severity CRITICAL,HIGH $IMAGE_NAME:$IMAGE_TAG || true
                '''
            }
        }

        stage('Push Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'ghcr-masjusufrh-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {

                    sh '''
                    echo $DOCKER_PASS | docker login ghcr.io -u $DOCKER_USER --password-stdin
                    docker push $IMAGE_NAME:$IMAGE_TAG
                    '''
                }
            }
        }

        /*stage('Deploy') {
            steps {
                sh '''
                sed -i "s|IMAGE_TAG|$IMAGE_TAG|g" k8s/deployment.yaml
                kubectl apply -f k8s/
                '''
            }
        }*/
    }
}