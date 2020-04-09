package com.github.shimo.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.process.ExecResult

/**
 * Use a (possibly private) github repo as a maven dependency.
 * @since 7/16/14
 * @author drapp , UnAfraid , shimodaira1986
 */
class GitRepoPlugin implements Plugin<Project> {
    static repoCache = [:]

    void apply(Project project) {
        // allow declaring special repositories
        if (!project.repositories.metaClass.respondsTo(project.repositories, 'github', String, String, String, String, Object)) {
            project.repositories.metaClass.github = { String org, String repo, String branch = "master", String type = "releases", def closure = null ->
                String gitUrl = "git@github.com:${org}/${repo}.git"
                def orgDir = repositoryDir(project, org)
                addLocalRepo(project, ensureLocalRepo(project, orgDir, repo, gitUrl, branch), type)
            }
        }

        if (!project.repositories.metaClass.respondsTo(project.repositories, 'bitbucket', String, String, String, String, Object)) {
            project.repositories.metaClass.bitbucket = { String org, String repo, String branch = "master", String type = "releases", def closure = null ->
                String gitUrl = "git@bitbucket.org:${org}/${repo}.git"
                def orgDir = repositoryDir(project, org)
                addLocalRepo(project, ensureLocalRepo(project, orgDir, repo, gitUrl, branch), type)
            }
        }

        if (!project.repositories.metaClass.respondsTo(project.repositories, 'git', String, String, String, String, Object)) {
            project.repositories.metaClass.git = { String gitUrl, String name, String branch = "master", String type = "releases", def closure = null ->
                def orgDir = repositoryDir(project, name)
                addLocalRepo(project, ensureLocalRepo(project, orgDir, name, gitUrl, branch), type)
            }
        }
    }

    private static File repositoryDir(Project project, String name) {
        if (project.hasProperty("gitRepoHome")) {
            return project.file("${project.property('gitRepoHome')}")
        } else {
            return project.file("$project.rootDir/.gitRepos")
        }
    }

    private static File ensureLocalRepo(Project project, File directory, String name, String gitUrl, String branch) {
        def repoDir = new File(directory, name)

        if (!repoDir.directory) {
            project.mkdir(repoDir)
            project.exec {
                workingDir repoDir
                executable "git"
                args "init"
            }

            project.exec {
                workingDir repoDir
                executable "git"
                args "remote", "add", "origin", gitUrl
            }
        }

        ExecResult fetchResult = project.exec {
            workingDir repoDir
            executable "git"
            args "fetch", "origin", branch, "--depth=1", "--progress"
            ignoreExitValue true
        }

        println "fetchResult=" + fetchResult
        if (fetchResult.exitValue != 0) {
            return repoDir;
        }

        println "checkout=" + project.exec {
            workingDir repoDir
            executable "git"
            args "checkout", "FETCH_HEAD", "-f"
            ignoreExitValue true
        }

        return repoDir
    }

    private static void addLocalRepo(Project project, File repoDir, String type) {
        project.repositories.maven {
            url repoDir.getAbsolutePath() + "/" + type
        }
    }
}