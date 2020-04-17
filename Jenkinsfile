pipeline {

	agent any

	// using the Timestamper plugin we can add timestamps to the console log
	options {
		timestamps()
		// keep only last 10 builds
		buildDiscarder(logRotator(numToKeepStr: '10'))
        // timeout job after 60 minutes
		timeout(time: 60, unit: 'MINUTES')
	}

	tools { 
		maven "maven"
		jdk "jdk8" 
	} 

	environment {
		//Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
		IMAGE = readMavenPom().getArtifactId()
		VERSION = readMavenPom().getVersion()
	}

	stages {
		stage('Prepare') {
			steps {
				echo 'Some preparing ...'
				sh 'rm -f -r ${WORKSPACE}/*'
			}
		}
		
		stage('Pull') {
			steps {
			    checkout scm: [$class: 'GitSCM', userRemoteConfigs: [[url: 'http://192.168.70.200/bio4j-spring/bio4j-spring-boot-starters.git', credentialsId: 'jenkins']], branches: [[name: "${params.VERSION_BRANCH}"]]], poll: false
			    //checkout scm: [$class: 'GitSCM', userRemoteConfigs: [[url: 'http://192.168.70.200/bio4j-spring/bio4j-spring-boot-starters.git', credentialsId: 'jenkins']], branches: [[name: "refs/tags/${params.VERSION_TAG}"]]], poll: false
				//git(url: 'http://192.168.70.200/bio4j-spring/bio4j-spring-boot-starters.git', branch: "refs/${params.VERSION_BRANCH}", credentialsId: 'jenkins')
				//git(url: 'http://192.168.70.200/bio4j-spring/bio4j-spring-boot-starters.git', branch: 'master', credentialsId: 'jenkins')
			}
		}

		stage('Build') {
			steps {
				sh 'mvn -Dmaven.test.skip=true clean install'
			}
		}
		stage('Publish') {
			steps {
				 echo 'sh cp /target/ ...'
			}
		}
	}
	
	post {
		/*
		* These steps will run at the end of the pipeline based on the condition.
		* Post conditions run in order regardless of their place in pipeline
		* 1. always - always run
		* 2. changed - run if something changed from last run
		* 3. aborted, success, unstable or failure - depending on status
		*/
		always {
			echo "I AM ALWAYS first"
		}
		changed {
			echo "CHANGED is run second"
		}
		aborted {
			echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED are exclusive of each other"
		}
		success {
			echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED runs last"
		}
		unstable {
			echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED runs last"
		}
		failure {
			echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED runs last"
		}
	}
}


