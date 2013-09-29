# Chaperone

Collaborative work environment with Chat, Audio and Video conferencing over WebRTC.

First installation will only only offer Chrome support. Firefox will come later.

Current status: Barely started.

## Installation

You will require a installation of [ElasticSearch](http://www.elasticsearch.org/)

To create the required index run `lein run -m chaperone.persistance.install` with the required environment variables.

## Enviornment Variables

 - ELASTICSEARCH_URL - The url of Elastic Search
 - ELATICSEARCH_INDEX - The name of the index to use for Chaperone.
 - WEB_SERVER_PORT - The port of the web server to run on. Defaults to 8080.

## Usage

FIXME: explanation

    $ java -jar chaperone-0.1.0-standalone.jar [args]

## Development / Test

There is a [VagrantFile](http://www.vagrantup.com/) that provides the test infrastructure you need to run an instance of chaperone.
Provisioning for Vagrant is provided by an [Ansible](http://www.ansibleworks.com/), that could also be reused to deploy to a production
instance.

### Compiling ClojureScript

Make sure to use `with-profile cljs` when interacting with Clojurescript. This is required to remove certain dependencies that otherwise
break compilation, and add in the clojurescript dependencies.

For example:
`lein with-profile cljs cljsbuild auto`

To automatically compile the ClojureScript.

## License

Copyright © 2013 Mark Mandel

Distributed under the Apache License, Version 2.0.
