# Chaperone





## Installation

You will require a installation of [ElasticSearch](http://www.elasticsearch.org/)

Chaperone will only automatically create an index, allowing you to manually create one, if you need more advanced ElasticSearch sharding/replication options.

The

## Usage

FIXME: explanation

    $ java -jar chaperone-0.1.0-standalone.jar [args]

## Development / Test

There is a [VagrantFile](http://www.vagrantup.com/) that provides the test infrastructure you need to run an instance of chaperone.
Provisioning for Vagrant is provided by an [Ansible](http://www.ansibleworks.com/), that could also be reused to deploy to a production
instance.

## License

Copyright © 2013 Mark Mandel

Distributed under the Apache License, Version 2.0.
