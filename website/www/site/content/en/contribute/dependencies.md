---
title: 'Dependencies Guide'
---
<!--
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# Dependencies Guide

This document describes policies for keeping Beam dependencies up to date.

Old dependencies cause user pain and can result in a system being unusable for some users. Many users do not use Beam in isolation and bundle other dependencies in the same deployment. These additional dependencies might pull in incompatible dependencies to user’s environment which can again result in broken Beam pipelines, sometimes with undefined behavior. To prevent this, users will have to update their deployment environment or worse yet may end up not being able to use Beam along with some of the other dependencies at all.

Beam Java SDK’s Gradle build defines a set of top level [dependencies](https://github.com/apache/beam/blob/master/buildSrc/src/main/groovy/org/apache/beam/gradle/BeamModulePlugin.groovy) and various components (runners, IO connectors, etc) can choose to include these dependencies. Components usually use the versions defined at the top level but may choose to override these versions.

If a component _X_ chooses to override the version of a dependency _D_ from _a_ to _b_ and another component _Y_ is incompatible with version _b_ of _D_, deployment of a user that uses both components _X_ and _Y_ will end up in a broken state.

A similar issue could arise if two dependencies of Beam depend on a common library but use incompatible versions of that library.

Also, users might not use Beam in isolation, a user that depends on Beam as well as other libraries in the same environment might run into similar issues if Beam and the other library share a dependency while using incompatible versions.

Beam Python SDK handles dependencies slightly differently, all dependencies are defined in a single [setup.py](https://github.com/apache/beam/blob/master/sdks/python/setup.py) file and are grouped. One of the groups describes required dependencies while other groups are for defining dependencies for various optional features. All Python modules have to use the versions of dependencies defined in [setup.py](https://github.com/apache/beam/blob/master/sdks/python/setup.py) file. Additionally, for most of the dependencies, Python SDK allows automatic upgrades upto next major version. Because of this setup, Python SDK currently does not run into component conflicts but other two forms of dependency conflicts described above can still occur.

This picture can become even more complicated during runtime. Runner specific code might be incompatible with dependencies included by certain modules and if these dependencies leak into runtime, a pipeline might end up in a broken state.

The overall issue is not unique to Beam and is well known in the industry as the [Diamond Dependency problem \(or Dependency Hell\)](https://en.wikipedia.org/wiki/Dependency_hell).

One common solution for the diamond dependency problem is [semantic versioning](https://semver.org/). The basic idea is that dependencies are versioned in the form _x.y.z_ where _x_ is the _major version_, _y_ is the _minor version_, and _z_ is the _patch version_. A major version change may be backwards incompatible and is expected to be rare. Minor and patch versions may be released more regularly but are expected to be backwards compatible. But in practice, important fixes (such as security patches) might get released in the form of minor or patch version updates and it will be healthy for the Beam project to depend on recently released minor versions of dependencies.

## Identifying outdated dependencies

A big part of keeping dependencies up to date involves identifying outdated dependencies of Beam that the community should try to upgrade.

Beam currently executes a weekly Jenkins job that tries to identify outdated dependencies for various SDKs. This Jenkins job generates a weekly report that is shared in Beam dev list.

In addition to this, Beam community members might identify other critical dependency updates that have to be manually performed. For example,
* A minor release of a dependency due to a critical security vulnerability.
* A dependency conflict that was was triggered by a minor version release of a Beam dependency (this does not apply to Java SDK that depends on exact minor versions of dependencies).

These kind of urgently required upgrades might not get automatically picked up by the Jenkins job for few months. So Beam community has to act to identify such issues and perform upgrades early.

## Dependabot Issue Automation

In order to track the dependency upgrade process, Dependabot will automatically raise pull requests to upgrade
outdated dependencies.

## Upgrading identified outdated dependencies

After outdated dependencies are identified, Beam community has to act to upgrade the dependencies regularly. Beam community has agreed on following policies regarding upgrading dependencies.

__Human readable reports on status of Beam dependencies are generated weekly by an automated Jenkins job and shared with the Beam community through the dev list.__

These reports should be concise and should highlight the cases where the community has to act on.

__Beam components should define dependencies and their versions at the top level. There can be rare exceptions, but they should come with explanations.__

Components include various Beam runners, IO connectors, etc. Component-level dependency version declarations should only be performed in rare cases and should come with a comment explaining the reasoning for overriding the dependency. For example, dependencies specific to a runner that are unlikely to be utilized by other components might be defined at the runner.

__A significantly outdated dependency (identified manually or through the automated Jenkins job) should result in a issue that is a blocker for the next release. Release manager may choose to push the blocker to the subsequent release or downgrade from a blocker.__

This will be a blocker for next major and minor version releases of Beam.

For manually identified critical dependency updates, Beam community members should create blocking Issues for next release. In addition to this Beam community members may trigger patch releases for any critical dependency fixes that should be made available to users urgently.

__Dependencies of Java SDK components that may cause issues to other components if leaked should be vendored.__

[Vendoring](https://www.ardanlabs.com/blog/2013/10/manage-dependencies-with-godep.html) is the process of creating copies of third party dependencies. Combined with repackaging, vendoring allows Beam components to depend on third party libraries without causing conflicts to other components. Vendoring should be done in a case-by-case basis since this can increase the total number of dependencies deployed in user's enviroment.

## Dependency updates and backwards compatibility

Beam [releases](/get-started/downloads/) generally follow the rules of semantic versioning. Hence, community members should take care when updating dependencies. Minor version updates to dependencies should be backwards compatible in most cases. Some updates to dependencies though may result in backwards incompatible API or functionality changes to Beam. PR reviewers and committers should take care to detect any dependency updates that could potentially introduce backwards incompatible changes to Beam before merging and PRs that update dependencies should include a statement regarding this verification in the form of a PR comment. Dependency updates that result in backwards incompatible changes to non-experimental features of Beam should be held till the next major version release of Beam. Any exceptions to this policy should only occur in extreme cases (for example, due to a security vulnerability of an existing dependency that is only fixed in a subsequent major version) and should be discussed on the Beam dev list. Note that backwards incompatible changes to experimental features may be introduced in a minor version release.
