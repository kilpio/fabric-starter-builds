    parameters {
        booleanParam(defaultValue: true, description: 'True if merge current ${MASTER_BRANCH} into stable', name: 'MERGE_FROM_MASTER')
        string(defaultValue: "stable", description: 'What brunch we are building', name: 'BUILD_BRANCH')
        string(defaultValue: "kilpio", description: 'take cources from https://github.com/${GIT_REPO_OWNER}/fabric-starter [fabric-starter-rest]', name: 'GIT_REPO_OWNER')
        credentials(name: 'GitHubCredentials', description: 'GitHub username with private key', defaultValue: '', credentialType: "SSH Username with private key", required: true )
        string(defaultValue: "https://registry-1.docker.io/v2", description: 'Docker registry we use', name: 'DOCKER_REGISTRY')
        string(defaultValue: "kilpio", description: 'Owner of the docker repo to push the built images', name: 'DOCKER_REPO')
        credentials(name: 'DockerCredentials', description: 'Docker Hub username and password', defaultValue: '', credentialType: "Username with password", required: true )
        string(defaultValue: "kilpio", description: 'Owner of the docker repo to get images to buils FS', name: 'FABRIC_STARTER_REPOSITORY')
        string(defaultValue: "master", description: 'Branch to merge into ${BUILD_BRANCH}', name: 'MASTER_BRANCH')
        string(defaultValue: "1.4.4", description: 'Fabric version', name: 'FABRIC_VERSION')
        }

    environment {
        GITHUB_SSH_CREDENTIALS_ID = credentials("${params.GitHubCredentials}")
        DOCKER_CREDENTIALS_ID = credentials("${params.DockerCredentials}")
    }  