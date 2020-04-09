# The Gradle Git Repo Plugin

This plugin allows you to add a git repository as a maven repo, even if the git
repository is private, similar to how CocoaPods works.

Using a github repo as a maven repo is a quick and easy way to host maven jars.
Private maven repos however, aren't easily accessible via the standard maven
http interface, or at least I haven't figured out how to get the authentication
right. This plugin simply clones the repo behind the scenes and uses it as a
local repo, so if you have permissions to clone the repo you can access it.

This plugin lets you tie access to your repository to github accounts, or any git repository
seamlessly. This is most useful if you've already set up to manage distribution
this way. Deliver CocoaPods and Maven artifacts with the same system, then sit
back and relax.

## Building

Run `gradle build` to build.

### Publish

Run `gradlew uploadArchives`, and upload the files in the `releases` directory.

## Usage
This plugin needs to be added via the standard plugin mechanism with this buildscript in your top level project
# Build script snippet for use in all Gradle versions
	buildscript {
		repositories {
			maven {
				url "https://github.com/shimodaira1986/gradle-git-repo-plugin/raw/master/releases"
			}
		}
		dependencies {
			classpath "com.github.shimo.gradle:gradle-git-repo-plugin:1.0.0"
		}
	}

and then apply the plugin

	apply plugin: "git-repo"


### Depending on git repos

This plug adds `github` and `git` methods to your repositories block

	repositories {
		github("layerhq", "maven-private", "master", "releases")
		git("https://some/clone/url.git", "arbitrary.unique.name", "master", "releases")
	}

Add either alongside other repositories and you're good to go. The `github` variant is
just a special case of `git`, they both do the same thing.

## Settings

The following gradle properties affect cloning dependencies

- **offline** when defined, no network operations will be performed, the repos will be assumed to be in place
- **gitRepoHome** the base directory for cloning git repos, {project.root}/.gitRepos by default

## License

The gradle git repo plugin is available under the Apache 2 License. See the LICENSE file for more info.
