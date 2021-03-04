pipelineJob("Fabric-Starter-Build") {
    description()
    keepDependencies(false)
    parameters {
        booleanParam("DEBUG", false, "Print extended build output and a final report in the build log")
        booleanParam("MERGE_FROM_MASTER", true, "True if merge current \${MASTER_BRANCH} into \${BUILD_BRANCH}")
        stringParam("BUILD_BRANCH", "stable", "What brunch we are building")
        stringParam("FABRIC_VERSION", "1.4.4", "Fabric version we use to build images")
        stringParam("PREVIOUS_FABRIC_VERSION", "1.4.9", "The Fabric version the stable branch was built, will be replaced with FABRIC_VERSION")
        credentialsParam("GITHUB_SSH_CREDENTIALS_ID") {
            description("GitHub username with private key")
            defaultValue()
            type("com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey")
            required(true)
        }
        credentialsParam("DOCKER_CREDENTIALS_ID") {
            description("Docker Hub username and password")
            defaultValue()
            type("com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl")
            required(true)
        }
        stringParam("GIT_REPO_OWNER", "kilpio", "Get sources from https://github.com/\${GIT_REPO_OWNER}/fabric-starter [fabric-starter-rest]")
        stringParam("DOCKER_REGISTRY", "", "Docker registry we use (now may be left empty foe dockerhub.io)")
        stringParam("DOCKER_REPO", "kilpio", "Owner of the docker repo to push the built images")
        stringParam("FABRIC_STARTER_REPOSITORY", "kilpio", "Owner of the git repo to get images to buils FS")
        stringParam("MASTER_BRANCH", "master", "Branch to merge into \${BUILD_BRANCH}")
        booleanParam("SKIP_DOCKER_PUSH", false, "True if we do not want to push images to docker")
        booleanParam("SKIP_FS_REST_BUILD", false, "True if we do not want to build and push Fabric Starter REST")
    }
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github("kilpio/fabric-starter-builds", "https")
                    }
                    branch("*")
                }
            }
            scriptPath("Jenkinsfile-no-latest")
        }
    }
    disabled(false)
    configure {
        it / 'properties' / 'com.coravy.hudson.plugins.github.GithubProjectProperty' {
            'projectUrl'('https://github.com/kilpio/fabric-starter-builds/')
            displayName()
        }
    }
}