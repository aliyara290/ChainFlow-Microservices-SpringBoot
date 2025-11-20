pipeline {
	agent any

	tools {
		maven 'Maven-3.9.9'
		jdk 'JDK-17'
	}

	environment {
		DOCKER_CREDENTIALS_ID = 'Dockerhub_Cred'
		SONAR_SUPPLY_SERVICE = credentials('SONAR_SUPPLY_SERVICE')
		SONAR_PRODUCTION_SERVICE = credentials('SONAR_PRODUCTION_SERVICE')
		SONAR_CUSTOMER_SERVICE = credentials('SONAR_CUSTOMER_SERVICE')
		DOCKER_REGISTRY = 'aliyara29'
	}

	stages {
		stage('Checkout') {
			steps {
				checkout scm
			}
		}

		stage('Build & Test Supply Service') {
			steps {
				dir('supply-service') {
					sh 'mvn clean verify -DskipITs sonar:sonar \
                     -Dsonar.host.url=http://host.docker.internal:9001 \
                     -Dsonar.projectKey=supply-service \
                     -Dsonar.login=$SONAR_SUPPLY_SERVICE'

					sh 'docker build -t $DOCKER_REGISTRY/supply-service:latest .'

					withCredentials([usernamePassword(
						credentialsId: env.DOCKER_CREDENTIALS_ID,
						usernameVariable: 'DOCKER_USERNAME',
						passwordVariable: 'DOCKER_PASSWORD'
					)]) {
						sh '''
                            echo "Logging into Docker Hub..."
                            echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                            echo "Pushing supply-service image..."
                            docker push $DOCKER_REGISTRY/supply-service:latest
                        '''
					}
				}
			}
		}

		stage('Build & Test Production Service') {
			steps {
				dir('production-service') {
					sh 'mvn clean verify -DskipITs sonar:sonar \
                     -Dsonar.host.url=http://host.docker.internal:9001 \
                     -Dsonar.projectKey=production-service \
                     -Dsonar.login=$SONAR_PRODUCTION_SERVICE'

					sh 'docker build -t $DOCKER_REGISTRY/production-service:latest .'

					withCredentials([usernamePassword(
						credentialsId: env.DOCKER_CREDENTIALS_ID,
						usernameVariable: 'DOCKER_USERNAME',
						passwordVariable: 'DOCKER_PASSWORD'
					)]) {
						sh '''
                            echo "Logging into Docker Hub..."
                            echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                            echo "Pushing production-service image..."
                            docker push $DOCKER_REGISTRY/production-service:latest
                        '''
					}
				}
			}
		}

		stage('Build & Test Customer Service') {
			steps {
				dir('customer-service') {
					sh 'mvn clean verify -DskipITs sonar:sonar \
                     -Dsonar.host.url=http://host.docker.internal:9001 \
                     -Dsonar.projectKey=customer-service \
                     -Dsonar.login=$SONAR_CUSTOMER_SERVICE'

					sh 'docker build -t $DOCKER_REGISTRY/customer-service:latest .'

					withCredentials([usernamePassword(
						credentialsId: env.DOCKER_CREDENTIALS_ID,
						usernameVariable: 'DOCKER_USERNAME',
						passwordVariable: 'DOCKER_PASSWORD'
					)]) {
						sh '''
                            echo "Logging into Docker Hub..."
                            echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                            echo "Pushing customer-service image..."
                            docker push $DOCKER_REGISTRY/customer-service:latest
                        '''
					}
				}
			}
		}
	}

	post {
		always {
			sh 'docker logout || true'
			cleanWs()
		}
	}
}