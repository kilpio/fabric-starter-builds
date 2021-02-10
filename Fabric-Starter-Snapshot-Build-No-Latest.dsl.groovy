pipelineJob("Fabric-Starter-Snapshot-Build-Test-No-Latest") {
    description()
    keepDependencies(false)
    parameters {
        booleanParam("MERGE_FROM_MASTER", true, "True if merge current \${MASTER_BRANCH} into stable")
        booleanParam("SKIP_DOCKER_PUSH", false, "True if we do not watn to push images to docker")
        booleanParam("SKIP_FS_REST_BUILD", false, "True if we do not want ot build and pus Fabric Starter REST")
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
        stringParam("BUILD_BRANCH", "stable", "What brunch we are building")
        stringParam("GIT_REPO_OWNER", "kilpio", "Get sources from https://github.com/\${GIT_REPO_OWNER}/fabric-starter [fabric-starter-rest]")
        stringParam("DOCKER_REGISTRY", "", "Docker registry we use")
        stringParam("DOCKER_REPO", "kilpio", "Owner of the docker repo to push the built images")
        stringParam("FABRIC_STARTER_REPOSITORY", "kilpio", "Owner of the git repo to get sources to build FS")
        stringParam("MASTER_BRANCH", "master", "Branch to merge into \${BUILD_BRANCH}")
        stringParam("MASTER_FABRIC_VERSION", "1.4.9", "master branch Fabric version")
        stringParam("FABRIC_VERSION", "1.4.4", "Fabric version")
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
}

listView("DSL Jobs") {
    jobs {
        name("Fabric-Starter-Snapshot-Build-No-Latest")
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}
