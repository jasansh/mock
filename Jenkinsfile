#!/usr/bin/env groovy
@Library('jenkinsPipelineSharedLibrary@release/artifactory_migration')

import com.tieto.fs.jenkins.pipeline.steps.utility.*

bootPipeline {
    mavenProject = '.'
    gitProject = 'testbankapps'
    gitUrl = 'ssh://git@bitbucket.shared.int.tds.tieto.com:2222/financialservices/testbankapps.git'
    disableSonarQubeStage = true
}
