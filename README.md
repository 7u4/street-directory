<!--- some badges to display on the GitHub page -->

![Travis (.org)](https://img.shields.io/travis/debuglevel/street-lister?label=Travis%20build)
![Gitlab pipeline status](https://img.shields.io/gitlab/pipeline/debuglevel/street-lister?label=GitLab%20build)
![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/debuglevel/street-lister?sort=semver)
![GitHub](https://img.shields.io/github/license/debuglevel/street-lister)
[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/debuglevel/street-lister)

# Street lister

This microservice extracts postal codes and streets inside them from OpenStreetMaps via Overpass API.

To do so, it first queries for all postal codes in an area (most useful a country) and then gets the streets for each
postal code area.

# HTTP API

## OpenAPI / Swagger

There is an OpenAPI (former: Swagger) specification created automatically on `./gradlew build`. It can easily be pasted
into the [Swagger Editor](https://editor.swagger.io) which provides a live demo
for [Swagger UI](https://swagger.io/tools/swagger-ui/), but also offers to create client libraries
via [OpenAPI Generator](https://openapi-generator.tech).

## Postal codes

### Start populating postal codes

```bash
$ curl --location --request POST 'http://localhost:8080/postalcodes/populate?areaId=3600051477'
```

Where `areaId` is the appropriate OSM area which meets some conditions, e.g.:

* Germany: 3600051477
* Bavaria: 3602145268
* Upper Frankonia: 3600017592

### Get postal codes

```bash
$ curl --location --request GET 'http://localhost:8080/postalcodes/'

[
    {
        "id": "83e73ae3-8f40-43b3-a6a4-6aedc163992b",
        "code": "91077",
        "centerLatitude": 49.6229874,
        "centerLongitude": 11.1421357,
        "note": "91077 Neunkirchen a. Brand",
        "createdOn": "2021-04-05T12:33:33.947",
        "lastModifiedOn": "2021-04-05T12:33:33.947"
    },
    [...]
]
```

## Streets

### Start populating streets

```bash
$ curl --location --request POST 'http://localhost:8080/streets/populate?areaId=3600051477'
```

### Get streets

```bash
$ curl --location --request GET 'http://localhost:8080/streets/'

[
    {
        "id": "61731e2c-32a9-4fdc-a314-c4719b2a668a",
        "postalcode": "91094",
        "streetname": "Hauptstraße",
        "centerLatitude": 49.6409168,
        "centerLongitude": 11.067322,
        "createdOn": "2021-04-05T12:39:17.098",
        "lastModifiedOn": "2021-04-05T12:39:18.234"
    },
    [...]
}
```

### Get streets by postal code

```bash
$ curl --location --request GET 'http://localhost:8080/streets/?postalcode=91094'

[
    {
        "id": "61731e2c-32a9-4fdc-a314-c4719b2a668a",
        "postalcode": "91094",
        "streetname": "Hauptstraße",
        "centerLatitude": 49.6409168,
        "centerLongitude": 11.067322,
        "createdOn": "2021-04-05T12:39:17.098",
        "lastModifiedOn": "2021-04-05T12:39:18.234"
    },
    [...]
}
```

### Get streets by postal code and street name

```bash
$ curl --location --request GET 'http://localhost:8080/streets/?postalcode=91094&streetname=Hauptstraße'

[
    {
        "id": "61731e2c-32a9-4fdc-a314-c4719b2a668a",
        "postalcode": "91094",
        "streetname": "Hauptstraße",
        "centerLatitude": 49.6409168,
        "centerLongitude": 11.067322,
        "createdOn": "2021-04-05T12:39:17.098",
        "lastModifiedOn": "2021-04-05T12:39:18.234"
    }
]
```

# Configuration

There is a `application.yml` included in the jar file. Its content can be modified and saved as a
separate `application.yml` on the level of the jar file. Configuration can also be applied via the other supported ways
of Micronaut (see <https://docs.micronaut.io/latest/guide/index.html#config>). For Docker, the configuration via
environment variables is the most interesting one (see `docker-compose.yml`).
