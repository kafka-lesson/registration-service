pipeline {
    agent any

     options {
        timestamps()
    }

    environment {
        IMAGE_NAME = "ghcr.io/masjusufrh/demo-kafka/registration-service"
        IMAGE_TAG = "${BUILD_NUMBER}"
        DOJO_TOKEN = credentials('DEFECTDOJO_API_TOKEN')
        DOJO_URL   = "http://10.28.224.218"
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

        stage('Trivy Vulnerability Scan') {
            steps {
                // Run Trivy and force the output to a specific JSON file
                sh '''
                echo "Running Trivy Scan..."
                trivy image --format json --output trivy-results.json ${IMAGE_NAME}:${IMAGE_TAG}
                '''
            }
        }

        stage('Upload to DefectDojo') {
            steps {
                sh '''
                echo "Checking if the file exists..."
                ls -la  # This will list all files in the Jenkins console log!
                
                if [ ! -f "trivy-results.json" ]; then
                    echo "ERROR: trivy-results.json was not found! The scan must have failed."
                    exit 1
                fi
                
                echo "Uploading to DefectDojo..."
                # Note: I removed the trailing slash from DOJO_URL to fix the double // in your logs
                curl -X POST "${DOJO_URL}api/v2/import-scan/" \
                  -H "Authorization: Token ${DOJO_TOKEN}" \
                  -F "scan_type=Trivy Scan" \
                  -F "file=@trivy-results.json" \
                  -F "product_name=My_Application" \
                  -F "engagement_name=Jenkins_Automated_Scan" \
                  -F "auto_create_context=true" \
                  -F "active=true" \
                  -F "verified=true"
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

        stage('Deploy') {
            steps {
                sh '''
                sed -i "s|IMAGE_TAG|$IMAGE_TAG|g" k8s/deployment.yaml
                kubectl apply -f k8s/
                '''
            }
        }
    }
}