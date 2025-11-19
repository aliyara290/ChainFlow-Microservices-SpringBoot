pipeline {
	agent any

	environment {
		DOCKER_CREDENTIALS = 'docker-hub-cred'
		SONAR_SUPPLY_SERVICE = credentials('supply-service')
		SONAR_PRODUCTION_SERVICE = credentials('production-service')
		SONAR_CUSTOMER_SERVICE = credentials('customer-service')
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
					sh 'mvn clean verify sonar:sonar -Dsonar.login=$SONAR_SUPPLY_SERVICE'
					script {
						docker.withRegistry('', DOCKER_CREDENTIALS) {
							def image = docker.build("$DOCKER_REGISTRY/supply-service:latest")
							image.push()
						}
					}
				}
			}
		}

		stage('Build & Test Production Service') {
			steps {
				dir('production-service') {
					sh 'mvn clean verify sonar:sonar -Dsonar.login=$SONAR_PRODUCTION_SERVICE'
					script {
						docker.withRegistry('', DOCKER_CREDENTIALS) {
							def image = docker.build("$DOCKER_REGISTRY/production-service:latest")
							image.push()
						}
					}
				}
			}
		}

		stage('Build & Test Customer Service') {
			steps {
				dir('customer-service') {
					sh 'mvn clean verify sonar:sonar -Dsonar.login=$SONAR_CUSTOMER_SERVICE'
					script {
						docker.withRegistry('', DOCKER_CREDENTIALS) {
							def image = docker.build("$DOCKER_REGISTRY/customer-service:latest")
							image.push()
						}
					}
				}
			}
		}
	}
}
